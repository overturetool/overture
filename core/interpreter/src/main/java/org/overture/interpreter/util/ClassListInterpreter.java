package org.overture.interpreter.util;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.definitions.ClassList;
import org.overture.interpreter.assistant.definition.ASystemClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;

public class ClassListInterpreter extends ClassList
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6070028869925906992L;

	public ClassListInterpreter(ClassList classList)
	{
		super();
		addAll(classList);
	}
	

	public ClassListInterpreter()
	{
		super();
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
			SClassDefinitionAssistantInterpreter.staticInit(cdef,globalContext);
		}

		// Values can forward reference each other, which means that we don't
		// know what order to initialize the classes in. So we have a crude
		// retry mechanism, looking for "forward reference" like exceptions.

		ContextException failed = null;
		int retries = 3;	// Potentially not enough.
		Set<ContextException> trouble = new HashSet<ContextException>();
		
		do
		{
			failed = null;
			trouble.clear();

    		for (SClassDefinition cdef: this)
    		{
    			try
    			{
    				SClassDefinitionAssistantInterpreter.staticValuesInit(cdef,globalContext);
    			}
    			catch (ContextException e)
    			{
    				trouble.add(e);
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

		if (!trouble.isEmpty())
		{
			ContextException toThrow = trouble.iterator().next();

			for (ContextException e: trouble)
			{
				Console.err.println(e);

				if (e.number != 4034)	// Not in scope err
				{
					toThrow = e;
				}
			}

			throw toThrow;
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
	
	public void setLoaded()
	{
		for (SClassDefinition d: this)
		{
			d.setTypeChecked(true);
		}
	}

	public int notLoaded()
	{
		int count = 0;

		for (SClassDefinition d: this)
		{
			if (!d.getTypeChecked()) count++;
		}

		return count;
	}
	
	public PDefinition findName(LexNameToken name, NameScope scope)
	{
		SClassDefinition d = map.get(name.module);

		if (d != null)
		{
			PDefinition def =PDefinitionAssistantInterpreter.findName(d,name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}
}
