package com.lausdahl.ast.creator;

import org.antlr.runtime.tree.CommonTree;

public class ExternalJavaClassDefinition extends CommonTreeClassDefinition
{
	String name;

	public ExternalJavaClassDefinition(CommonTree thisClass,
			IClassDefinition superClass, ClassType type, String name,Environment env)
	{
		super(thisClass, superClass, type,env);
		if(name.contains("."))
		{
			setPackageName(name.substring(0,name.lastIndexOf(".")));
			this.name=name.substring(name.lastIndexOf(".")+1);
		}else
		{
		this.name = name;
		}
	}

	@Override
	public String getName()
	{
		return name ;
	}

	@Override
	public String getJavaSourceCode()
	{
		return "";
	}

	@Override
	public String getVdmSourceCode()
	{
		return "";
	}
	
	@Override
	public String toString()
	{
			return getName();
	}
}
