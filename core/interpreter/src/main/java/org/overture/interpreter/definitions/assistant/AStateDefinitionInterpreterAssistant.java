package org.overture.interpreter.definitions.assistant;

import org.overture.interpreter.ast.definitions.AStateDefinitionInterpreter;
import org.overturetool.vdmj.runtime.Context;

public class AStateDefinitionInterpreterAssistant
{

	public static Context getStateContext(AStateDefinitionInterpreter state)
	{
		return state.getModuleState().getContext();
	}

}
