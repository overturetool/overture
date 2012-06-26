package org.overture.interpreter.runtime.state;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.interpreter.values.ObjectValue;


public class ASystemClassDefinitionRuntime extends SClassDefinitionRuntime
{
	public ASystemClassDefinitionRuntime(ASystemClassDefinition def)
	{
		super(def);
	}

	public static ObjectValue system = null;
}
