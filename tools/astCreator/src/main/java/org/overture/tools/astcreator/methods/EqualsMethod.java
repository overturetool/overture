package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;

public class EqualsMethod extends Method
{

	public EqualsMethod(IClassDefinition c)
	{
		super(c);
	}

	@Override
	protected void prepare(Environment env)
	{
		this.name = "equals";
		this.javaDoc = "\t/**\n\t* Essentially this.toString().equals(o.toString()).\n\t**/";
		this.annotation = "@Override";
		this.returnType = "boolean";
		this.arguments.add(new Argument("Object", "o"));
		StringBuffer sb = new StringBuffer();
		sb.append("\t\tif (o != null && o instanceof "	+ classDefinition.getName().getName() + ")");
		sb.append("\t\t{\n");
		sb.append("\t\t\t return toString().equals(o.toString());\n");
		sb.append("\t\t}\n");
		sb.append("\t\treturn false;");
		this.body = sb.toString();

	}

}
