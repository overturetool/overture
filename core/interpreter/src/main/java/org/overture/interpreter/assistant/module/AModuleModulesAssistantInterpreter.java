package org.overture.interpreter.assistant.module;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.assistant.definition.AStateDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.StateContext;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.module.AModuleModulesAssistantTC;

public class AModuleModulesAssistantInterpreter extends
		AModuleModulesAssistantTC
{

	public static Context getStateContext(AModuleModules defaultModule)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Initialize the system for execution from this module. The initial
	 * {@link Context} is created, and populated with name/value pairs from the
	 * local definitions and the imported definitions. If state is defined
	 * by the module, this is also initialized, creating the state Context.
	 *
	 * @return True if initialized OK.
	 */
	public static Set<ContextException> initialize(AModuleModules m,
			StateContext initialContext)
	{
		
		Set<ContextException> trouble = new HashSet<ContextException>();

		for (PDefinition d: m.getImportdefs())
		{
			if (d instanceof ARenamedDefinition)
			{
				try
				{
					initialContext.putList(PDefinitionAssistantInterpreter.getNamedValues(d,initialContext));
				}
				catch (ContextException e)
				{
					trouble.add(e);		// Carry on...
				}
			}
		}

		for (PDefinition d: m.getDefs())
		{
			try
			{
				initialContext.putList(PDefinitionAssistantInterpreter.getNamedValues(d,initialContext));
			}
			catch (ContextException e)
			{
				trouble.add(e);		// Carry on...
			}
		}

		try
		{
			AStateDefinition sdef = PDefinitionListAssistantInterpreter.findStateDefinition(m.getDefs());

			if (sdef != null)
			{
				AStateDefinitionAssistantInterpreter.initState(sdef,initialContext);
			}
		}
		catch (ContextException e)
		{
			trouble.add(e);		// Carry on...
		}

		return trouble;
		
	}

	public static ProofObligationList getProofObligations(
			AModuleModules m)
	{
		return PDefinitionListAssistantInterpreter.getProofObligations(m.getDefs(),new POContextStack());
	}

}
