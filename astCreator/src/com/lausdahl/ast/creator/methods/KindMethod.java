package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.IClassDefinition;
import com.lausdahl.ast.creator.IClassDefinition.ClassType;

public class KindMethod extends Method
{

	CommonTreeClassDefinition c;

	public KindMethod(CommonTreeClassDefinition c,Environment env)
	{
		super(c,env);
		this.c = c;
	}

	@Override
	protected void prepare()
	{

		if (c.getType() == ClassType.Production)
		{
			this.isAbstract = true;
			this.name = "kind" + c.getName();
			this.returnType = c.getEnumTypeName();
		} else
		{
			IClassDefinition superClass = c.getSuperClassDefinition();
			if(superClass instanceof CommonTreeClassDefinition)
			{
			String enumerationName = ((CommonTreeClassDefinition)superClass).getEnumTypeName();
			this.name = "kind" + c.getSuperClassDefinition().getName();

			// this.arguments.add(new Argument(f.getType(), "value"));
			this.returnType = enumerationName;
			this.annotation = "@Override";
			this.body = "\t\treturn " + enumerationName + "." + c.getEnumName()
					+ ";";
			}
		}

		javaDoc = "\t/**\n";
		javaDoc += "\t * Returns the {@link " + this.returnType
				+ "} corresponding to the\n";
		javaDoc += "\t * type of this {@link " + this.returnType + "} node.\n";
		javaDoc += "\t * @return the {@link " + this.returnType
				+ "} for this node\n";
		javaDoc += "\t */";
	}

	@Override
	protected void prepareVdm()
	{
		skip = true;
	}
}
