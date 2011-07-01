package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

public class PredefinedClassDefinition implements IClassDefinition
{
	private String packageName;
	private String name;
	private String namePostfix = "";
	private String tag = "";
	private boolean frozenName = false;

	public PredefinedClassDefinition(String packageName, String name)
	{
		this.packageName = packageName;
		this.name = name;
	}
	
	public PredefinedClassDefinition(String packageName, String name,boolean frozenName)
	{
		this.packageName = packageName;
		this.name = name;
		this.frozenName = frozenName;
	}

	@Override
	public List<String> getImports()
	{
		return new Vector<String>();
	}

	@Override
	public boolean isFinal()
	{
		return false;
	}

	@Override
	public boolean isAbstract()
	{
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
	public String getName()
	{
		return name + (frozenName?"":getNamePostfix());
	}

	@Override
	public String getSignatureName()
	{
		return getName();
	}

	@Override
	public String getSuperSignatureName()
	{
		return null;
	}

	@Override
	public List<Field> getFields()
	{
		return null;
	}

	@Override
	public boolean hasSuper()
	{
		return false;
	}

	@Override
	public void addField(Field field)
	{
	}

	@Override
	public void setNamePostfix(String postfix)
	{
		this.namePostfix = postfix;
	}

	@Override
	public String getNamePostfix()
	{
		return namePostfix;
	}

	@Override
	public IClassDefinition getSuperDef()
	{
		return null;
	}

	@Override
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	@Override
	public String getTag()
	{
		return this.tag;
	}

	@Override
	public void setGenericArguments(IInterfaceDefinition... arguments)
	{
		
	}

	@Override
	public List<IInterfaceDefinition> getGenericArguments()
	{
		return new Vector<IInterfaceDefinition>();
	}

	@Override
	public void setGenericArguments(List<IInterfaceDefinition> arguments)
	{
		
	}

	@Override
	public void setAnnotation(String annotation)
	{
		
	}
}
