package org.overture.interpreter.util;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.definitions.ClassList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

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

	public void systemInit(ResourceScheduler scheduler, DBGPReader dbgp,
			RootContext initialContext)
	{
		ASystemClassDefinition systemClass = null;

		for (SClassDefinition cdef : this)
		{
			if (cdef instanceof ASystemClassDefinition)
			{
				systemClass = (ASystemClassDefinition) cdef;
				initialContext.assistantFactory.createASystemClassDefinitionAssistant().systemInit(systemClass, scheduler, dbgp, initialContext);
				TransactionValue.commitAll();
			}
		}
	}

	public RootContext initialize(IInterpreterAssistantFactory af,
			DBGPReader dbgp)
	{
		StateContext globalContext = null;

		if (isEmpty())
		{
			globalContext = new StateContext(af, new LexLocation(), "global environment");
		} else
		{
			globalContext = new StateContext(af, this.get(0).getLocation(), "public static environment");
		}

		globalContext.setThreadState(dbgp, CPUValue.vCPU);

		// Initialize all the functions/operations first because the values
		// "statics" can call them.

		for (SClassDefinition cdef : this)
		{
			af.createSClassDefinitionAssistant().staticInit(cdef, globalContext);
		}

		// Values can forward reference each other, which means that we don't
		// know what order to initialize the classes in. So we have a crude
		// retry mechanism, looking for "forward reference" like exceptions.

		ContextException failed = null;
		int retries = 3; // Potentially not enough.
		Set<ContextException> trouble = new HashSet<ContextException>();

		do
		{
			failed = null;
			trouble.clear();

			for (SClassDefinition cdef : this)
			{
				try
				{
					af.createSClassDefinitionAssistant().staticValuesInit(cdef, globalContext);
				} catch (ContextException e)
				{
					trouble.add(e);
					// These two exceptions mean that a member could not be
					// found, which may be a forward reference, so we retry...

					if (e.number == 4034 || e.number == 6)
					{
						failed = e;
					} else
					{
						throw e;
					}
				}
			}
		} while (--retries > 0 && failed != null);

		if (!trouble.isEmpty())
		{
			ContextException toThrow = trouble.iterator().next();

			for (ContextException e : trouble)
			{
				Console.err.println(e);

				if (e.number != 4034) // Not in scope err
				{
					toThrow = e;
				}
			}

			throw toThrow;
		}

		return globalContext;
	}

	public ProofObligationList getProofObligations(
			IInterpreterAssistantFactory assistantFactory)
			throws AnalysisException
	{
		// TODO: Check this method, where it is used.
		ProofObligationList obligations = new ProofObligationList();

		for (SClassDefinition c : this)
		{

			obligations.addAll(ProofObligationGenerator.generateProofObligations(c));
		}
		obligations.trivialCheck();
		return obligations;
	}

	public void setLoaded()
	{
		for (SClassDefinition d : this)
		{
			d.setTypeChecked(true);
		}
	}

	public int notLoaded()
	{
		int count = 0;

		for (SClassDefinition d : this)
		{
			if (!d.getTypeChecked())
			{
				count++;
			}
		}

		return count;
	}

	public PDefinition findName(LexNameToken name, NameScope scope)
	{
		SClassDefinition d = map.get(name.module);

		if (d != null)
		{
			PDefinition def = ClassInterpreter.getInstance().getAssistantFactory().createPDefinitionAssistant().findName(d, name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}
}
