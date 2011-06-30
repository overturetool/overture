package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.Environment;

public class ExternalJavaClassDefinition extends CommonTreeClassDefinition
{
	String name;

	public ExternalJavaClassDefinition(String rawName ,
			IClassDefinition superClass, ClassType type, String name,Environment env)
	{
		super(rawName, superClass, type,env);
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
	
	@Override
	public boolean hasSuper()
	{
	return false;
	}
}
