package org.overture.interpreter.runtime.state;

import org.overture.interpreter.runtime.IRuntimeState;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.State;

public class StateDefinitionRuntimeState implements IRuntimeState
{
	public FunctionValue invfunc = null;
	public FunctionValue initfunc = null;
	public State moduleState = null;
}
