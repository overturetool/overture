package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;



public class PredefinedClassDefinition implements IClassDefinition
{
	private String packageName;
	private String name;

	public PredefinedClassDefinition(String packageName, String name)
	{
this.packageName = packageName;
this.name = name;
	}

	@Override
	public List<String> getImports()
	{
		return new Vector<String>();
	}

	@Override
	public boolean isFinal()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPackageName()
	{
		return packageName;
	}

	@Override
	public void setPackageName(String packageName)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getJavaSourceCode()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getVdmSourceCode()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getName()
	{
return name;
	}

	@Override
	public String getSignatureName()
	{
		return name;
	}

	@Override
	public String getSuperSignatureName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> getFields()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSuper()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSuperName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addField(Field field)
	{
		// TODO Auto-generated method stub
		
	}

}
