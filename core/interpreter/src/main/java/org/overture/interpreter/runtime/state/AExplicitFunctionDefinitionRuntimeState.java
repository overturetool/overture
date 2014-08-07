package org.overture.interpreter.runtime.state;

import java.util.List;
import java.util.Map;

import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.IRuntimeState;
import org.overture.interpreter.values.FunctionValue;

public class AExplicitFunctionDefinitionRuntimeState implements IRuntimeState
{
	public Map<List<PType>, FunctionValue> polyfuncs = null;
}
