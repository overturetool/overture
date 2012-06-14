package org.overture.interpreter.assistant.module;

import java.util.Set;

import org.overture.ast.modules.AModuleModules;
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

	public static Set<ContextException> initialize(AModuleModules m,
			StateContext initialContext)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static ProofObligationList getProofObligations(
			AModuleModules m)
	{
		return PDefinitionListAssistantInterpreter.getProofObligations(m.getDefs(),new POContextStack());
	}

}
