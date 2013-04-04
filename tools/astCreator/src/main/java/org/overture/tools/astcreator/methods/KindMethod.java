package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.utils.EnumUtil;

public class KindMethod extends Method
{

	boolean isAbstractKind = false;

	public KindMethod(IClassDefinition c, boolean isAbstractKind
			)
	{
		super(c);
		this.isAbstractKind = isAbstractKind;
	}

	@Override
	protected void prepare(Environment env)
	{
		if (isAbstractKind)// (c.getType() == ClassType.Production)
		{
			this.isAbstract = true;
			this.name = "kind"
					+ env.getInterfaceForCommonTreeNode(classDefinition).getName()
					.getName();
			this.returnType = "String";
		} else
		{
			IClassDefinition superClass = classDefinition.getSuperDef();
			if (env.isTreeNode(superClass))
			{
				this.name = "kind"
						+ env
						.getInterfaceForCommonTreeNode(
								classDefinition.getSuperDef()).getName().getName();

				// this.arguments.add(new Argument(f.getType(), "value"));
				this.returnType = "String";
				this.annotation = "@Override";

				this.body = "\t\treturn " + this.name + ";";
			}
		}
			javaDoc = "\t/**@Deprecated We wish to get rid of all enum.\n";
			javaDoc += "\t * Returns the {@link " + this.returnType
					+ "} corresponding to the\n";
			javaDoc += "\t * type of this {@link " + this.returnType + "} node.\n";
			javaDoc += "\t * @return the {@link " + this.returnType
					+ "} for this node\n";
			javaDoc += "\t */";
	}

	@Override
	protected void prepareVdm(Environment env)
	{
		skip = true;
	}
}
