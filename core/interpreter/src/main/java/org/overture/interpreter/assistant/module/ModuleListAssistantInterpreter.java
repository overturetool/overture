package org.overture.interpreter.assistant.module;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.modules.ModuleList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.CollectedContextException;
import org.overture.interpreter.runtime.CollectedExceptions;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligationList;

public class ModuleListAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ModuleListAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public RootContext createInitialContext(ModuleList modules)
	{
		StateContext initialContext = null;

		if (modules.isEmpty())
		{
			initialContext = new StateContext(af, new LexLocation(), "global environment");
		}
		else
		{
			initialContext = new StateContext(af, modules.get(0).getName().getLocation(), "global environment");
		}
		
		return initialContext;
	}

	public void initialize(RootContext ctxt, ModuleList modules, DBGPReader dbgp)
	{
		StateContext initialContext = (StateContext) ctxt;
		initialContext.setThreadState(dbgp, null);
		Set<ContextException> problems = null;
		int retries = 5;

		do
		{
			problems = new HashSet<ContextException>();

			for (AModuleModules m : modules)
			{
				Set<ContextException> e = af.createAModuleModulesAssistant().initialize(m, initialContext);

				if (e != null)
				{
					problems.addAll(e);
				}
			}
		} while (--retries > 0 && !problems.isEmpty());

		if (!problems.isEmpty())
		{
			ContextException toThrow = problems.iterator().next();

			for (ContextException e : problems)
			{
				Console.err.println(e);

				if (e.number != 4034) // Not in scope err
				{
					toThrow = e;
				}
			}

			// throw toThrow;
			if (toThrow instanceof ContextException)
			{
				throw new CollectedContextException((ContextException) toThrow, problems);
			} else
			{
				throw new CollectedExceptions(problems);
			}
		}
	}

	public IProofObligationList getProofObligations(ModuleList modules)
			throws AnalysisException
	{

		IProofObligationList obligations = new ProofObligationList();

		for (AModuleModules m : modules)
		{
			obligations.addAll(af.createAModuleModulesAssistant().getProofObligations(m));
		}

		obligations.trivialCheck();
		return obligations;
	}

}
