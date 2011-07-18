package com.lausdahl.ast.creator.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class GenericArgumentedIInterfceDefinition implements
		IInterfaceDefinition
{
	IInterfaceDefinition def;
	List<IInterfaceDefinition> arguments = new Vector<IInterfaceDefinition>();

	public GenericArgumentedIInterfceDefinition(IInterfaceDefinition def,
			IInterfaceDefinition... arguments)
	{
		this.def = def;
		setGenericArguments(arguments);
	}

	
	public String getName()
	{
		String tmp =def.getSignatureName()+"<";
		for (IInterfaceDefinition arg : arguments)
		{
			tmp+=arg.getName()+", ";
		}
		if(!arguments.isEmpty())
		{
			tmp = tmp.substring(0,tmp.length()-2);
		}
		return tmp+">";
	}

	
	public Set<String> getImports()
	{
		return def.getImports();
	}

	
	public boolean isFinal()
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isAbstract()
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getPackageName()
	{
		return def.getPackageName();
	}

	
	public void setPackageName(String packageName)
	{
		// TODO Auto-generated method stub

	}

	
	public String getSignatureName()
	{
		return def.getSignatureName();
	}

	
	public String getJavaSourceCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getVdmSourceCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setNamePostfix(String postfix)
	{
		// TODO Auto-generated method stub

	}

	
	public String getNamePostfix()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setTag(String tag)
	{
		// TODO Auto-generated method stub

	}

	
	public String getTag()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setGenericArguments(IInterfaceDefinition... arguments)
	{
		if (arguments != null)
		{
			this.arguments.addAll(Arrays.asList(arguments));
		}
	}

	
	public List<IInterfaceDefinition> getGenericArguments()
	{
		return this.arguments;
	}

	
	public void setGenericArguments(List<IInterfaceDefinition> arguments)
	{
		if (arguments != null)
		{
			this.arguments.addAll(arguments);
		}
	}

	
	public void setAnnotation(String annotation)
	{
		
	}


	public String getImportName()
	{
		return getPackageName() + "." + getSignatureName();
	}

}
