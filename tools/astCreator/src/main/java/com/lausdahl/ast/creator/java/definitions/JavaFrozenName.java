package com.lausdahl.ast.creator.java.definitions;

public class JavaFrozenName extends JavaName
{

	public JavaFrozenName(String packageName, String... name)
	{
		super(packageName, name);
	}
	
	@Override
	public void setPackageName(String packageName)
	{
	}
	
	@Override
	public void setPostfix(String postfix)
	{
	}
	
	@Override
	public void setPrefix(String prefix)
	{
	}

}
