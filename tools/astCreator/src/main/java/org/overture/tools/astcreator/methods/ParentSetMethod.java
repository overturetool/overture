package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.env.Environment;

public class ParentSetMethod extends Method
{
	Field f;
	String structureClassName;
	public ParentSetMethod(String structureClassName, Field f)
	{
		super(null);
		this.f = f;
		this.structureClassName = structureClassName;
	}
	
	@Override
	protected void prepare(Environment env)
	{
		name = "parent";
		arguments.add(new Method.Argument(structureClassName, "value"));
		body = "\t\tthis." + f.getName(env) + " = value;";
	}

}
