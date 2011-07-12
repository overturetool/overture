package com.lausdahl.ast.creator.definitions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.ToStringAddOn;

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

	
	public Set<String> getImports()
	{
		return new HashSet<String>();
	}

	
	public boolean isFinal()
	{
		return false;
	}

	
	public boolean isAbstract()
	{
		return false;
	}

	
	public String getPackageName()
	{
		return packageName;
	}

	
	public void setPackageName(String packageName)
	{

	}

	
	public String getJavaSourceCode()
	{
		return "";
	}

	
	public String getVdmSourceCode()
	{
		return "";
	}

	
	public String getName()
	{
		return name + (frozenName?"":getNamePostfix());
	}

	
	public String getSignatureName()
	{
		return getName();
	}

	
	public String getSuperSignatureName()
	{
		return null;
	}

	
	public List<Field> getFields()
	{
		return null;
	}

	
	public boolean hasSuper()
	{
		return false;
	}

	
	public void addField(Field field)
	{
	}

	
	public void setNamePostfix(String postfix)
	{
		this.namePostfix = postfix;
	}

	
	public String getNamePostfix()
	{
		return namePostfix;
	}

	
	public IClassDefinition getSuperDef()
	{
		return null;
	}

	
	public void setTag(String tag)
	{
		this.tag = tag;
	}

	
	public String getTag()
	{
		return this.tag;
	}

	
	public void setGenericArguments(IInterfaceDefinition... arguments)
	{
		
	}

	
	public List<IInterfaceDefinition> getGenericArguments()
	{
		return new Vector<IInterfaceDefinition>();
	}

	
	public void setGenericArguments(List<IInterfaceDefinition> arguments)
	{
		
	}

	
	public void setAnnotation(String annotation)
	{
		
	}

	
	public Set<IInterfaceDefinition> getInterfaces()
	{
		return new HashSet<IInterfaceDefinition>();
	}

	public void addToStringAddOn(ToStringAddOn addon)
	{
		
	}

	public List<ToStringAddOn> getToStringAddOns()
	{
		return new Vector<ToStringAddOn>();
	}
}
