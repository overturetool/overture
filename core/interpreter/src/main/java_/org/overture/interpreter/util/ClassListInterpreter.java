package org.overture.interpreter.util;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.util.definitions.ClassList;
import org.overture.interpreter.assistant.definition.ASystemClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.ASystemClassDefinitionAssistantTC;

public class ClassListInterpreter extends ClassList
{

	public ClassListInterpreter(ClassList classList)
	{
		super();
		addAll(classList);
	}
	

	public void systemInit(ResourceScheduler scheduler, DBGPReader dbgp, RootContext initialContext)
	{
		ASystemClassDefinition systemClass = null;

		for (SClassDefinition cdef: this)
		{
			if (cdef instanceof ASystemClassDefinition)
			{
				systemClass = (ASystemClassDefinition)cdef;
				ASystemClassDefinitionAssistantInterpreter.systemInit(systemClass,scheduler, dbgp, initialContext);
				TransactionValue.commitAll();
			}
		}
	}

	public RootContext initialize(DBGPReader dbgp)
	{
		StateContext globalContext = null;

		if (isEmpty())
		{
			globalContext = new StateContext(
				new LexLocation(), "global environment");
		}
		else
		{
			globalContext =	new StateContext(
				this.get(0).getLocation(), "public static environment");
		}

		globalContext.setThreadState(dbgp, CPUValue.vCPU);

		// Initialize all the functions/operations first because the values
		// "statics" can call them.

		for (SClassDefinition cdef: this)
		{
			cdef.staticInit(globalContext);
		}

		// Values can forward reference each other, which means that we don't
		// know what order to initialize the classes in. So we have a crude
		// retry mechanism, looking for "forward reference" like exceptions.

		ContextException failed = null;
		int retries = 3;	// Potentially not enough.

		do
		{
			failed = null;

    		for (SClassDefinition cdef: this)
    		{
    			try
    			{
    				cdef.staticValuesInit(globalContext);
    			}
    			catch (ContextException e)
    			{
    				// These two exceptions mean that a member could not be
    				// found, which may be a forward reference, so we retry...

    				if (e.number == 4034 || e.number == 6)
    				{
    					failed = e;
    				}
    				else
    				{
    					throw e;
    				}
    			}
    		}
		}
		while (--retries > 0 && failed != null);

		if (failed != null)
		{
			throw failed;
		}

		return globalContext;
	}
	
	public ProofObligationList getProofObligations()
	{
		ProofObligationList obligations = new ProofObligationList();

		for (SClassDefinition c: this)
		{
			obligations.addAll(SClassDefinitionAssistantInterpreter.getProofObligations(c,new POContextStack()));
		}

		obligations.trivialCheck();
		return obligations;
	}
}
