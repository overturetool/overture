package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class KindMethod extends Method
{

	CommonTreeClassDefinition c;
	boolean isAbstractKind = false;

	public KindMethod(CommonTreeClassDefinition c,boolean isAbstractKind, Environment env)
	{
		super(c, env);
		this.c = c;
		this.isAbstractKind = isAbstractKind;
	}

	@Override
	protected void prepare()
	{

		if (isAbstractKind)//(c.getType() == ClassType.Production)
		{
			this.isAbstract = true;
			this.name = "kind" + c.getName();
			this.returnType = c.getEnumTypeName();
		} else
		{
			IClassDefinition superClass = c.getSuperClassDefinition();
			if (superClass instanceof CommonTreeClassDefinition)
			{
				String enumerationName = ((CommonTreeClassDefinition) superClass).getEnumTypeName();
				this.name = "kind" + c.getSuperClassDefinition().getName();

				// this.arguments.add(new Argument(f.getType(), "value"));
				this.returnType = enumerationName;
				this.annotation = "@Override";
				this.body = "\t\treturn " + enumerationName + "."
						+ c.getEnumName() + ";";
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
