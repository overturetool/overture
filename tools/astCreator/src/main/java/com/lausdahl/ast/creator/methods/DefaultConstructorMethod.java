package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class DefaultConstructorMethod extends Method
{
	public DefaultConstructorMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = c.getSignatureName();
		this.returnType = "";

		javaDoc = "\t/**\n";
		javaDoc += "\t * Creates a new {@link "+c.getName()+"} node with no children.\n";
		javaDoc += "\t */";
		// this.arguments.add(new Argument(f.getType(), "value"));

		// StringBuilder sb = new StringBuilder();
		//
		// if (!f.isTokenField && !f.isAspect)
		// {
		// sb.append("\t\tif (this." + f.getName() + " != null) {\n");
		// sb.append("\t\t\tthis." + f.getName() + ".parent(null);\n");
		// sb.append("\t\t}\n");
		// sb.append("\t\tif (value != null) {\n");
		// sb.append("\t\t\tif (value.parent() != null) {\n");
		// sb.append("\t\t\t\tvalue.parent().removeChild(value);\n");
		// sb.append("\t\t}\n");
		// sb.append("\t\t\tvalue.parent(this);\n");
		// sb.append("\t\t}\n");
		// }
		// sb.append("\t\tthis." + f.getName() + " = value;\n");

		this.body = "";// sb.toString();
	}
}
