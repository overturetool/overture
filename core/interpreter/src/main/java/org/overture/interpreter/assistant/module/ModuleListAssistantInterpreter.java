package org.overture.interpreter.assistant.module;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.modules.ModuleList;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.pog.obligation.ProofObligationList;

public class ModuleListAssistantInterpreter
{	
	
	public static RootContext initialize(ModuleList modules, DBGPReader dbgp)
	{
		StateContext initialContext = null;

		if (modules.isEmpty())
		{
			initialContext = new StateContext(
				new LexLocation(), "global environment");
		}
		else
		{
			initialContext =
				new StateContext(modules.get(0).getName().location, "global environment");
		}

		initialContext.setThreadState(dbgp, null);
		Set<ContextException> problems = null;
		int retries = 5;

		do
		{
			problems = new HashSet<ContextException>();

        	for (AModuleModules m: modules)
    		{
        		Set<ContextException> e = AModuleModulesAssistantInterpreter.initialize(m,initialContext);

        		if (e != null)
        		{
        			problems.addAll(e);
        		}
     		}
		}
		while (--retries > 0 && !problems.isEmpty());

		if (!problems.isEmpty())
		{
			ContextException toThrow = problems.iterator().next();

			for (ContextException e: problems)
			{
				Console.err.println(e);

				if (e.number != 4034)	// Not in scope err
				{
					toThrow = e;
				}
			}

			throw toThrow;
		}

		return initialContext;
	}

	
	
	public static ProofObligationList getProofObligations(ModuleList modules)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (AModuleModules m: modules)
		{
			obligations.addAll(AModuleModulesAssistantInterpreter.getProofObligations(m));
		}

		obligations.trivialCheck();
		return obligations;
	}

}
