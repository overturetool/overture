package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.utils.NameUtil;

public class KindString extends Method
{

	boolean isAbstractKind = false;

	public KindString(IClassDefinition c, boolean isAbstractKind)
	{
		super(c);
		this.isAbstractKind = isAbstractKind;
	}

	@Override
	protected void prepare(Environment env)
	{
		if (isAbstractKind)// (c.getType() == ClassType.Production)
		{
			this.skip = true;
			this.isAbstract = true;
			this.name = "skipme";
			this.returnType = "";
		} else
		{
			IClassDefinition superClass = classDefinition.getSuperDef();
			if (env.isTreeNode(superClass))
			{
				this.isAbstract = true; // ensure no body
				this.name = "kind"
						+ env
						.getInterfaceForCommonTreeNode(
								classDefinition.getSuperDef()).getName().getName();

				// this.arguments.add(new Argument(f.getType(), "value"));
				this.returnType = "String";
				this.body = "\""
						+ NameUtil.getClassName(
								classDefinition.getName().getRawName().startsWith("#")
								? classDefinition.getName().getRawName().substring(1)
								: classDefinition.getName().getRawName())
						+ "\"";
			}
		}
	}

	@Override
	public String getSignature(Environment env) {
		internalPrepare(env);
		return "\tpublic static final " + returnType.trim() + " " + name + " = " + body;
	}
	
	@Override
	protected void prepareVdm(Environment env)
	{
		skip = true;
	}
}
