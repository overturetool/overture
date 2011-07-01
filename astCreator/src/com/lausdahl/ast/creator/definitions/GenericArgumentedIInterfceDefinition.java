package com.lausdahl.ast.creator.definitions;

import java.util.Arrays;
import java.util.List;
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

	@Override
	public String getName()
	{
		String tmp =def.getSignatureName()+"<";
		for (IInterfaceDefinition arg : arguments)
		{
			tmp+=arg.getSignatureName()+", ";
		}
		if(!arguments.isEmpty())
		{
			tmp = tmp.substring(0,tmp.length()-2);
		}
		return tmp+">";
	}

	@Override
	public List<String> getImports()
	{
		return def.getImports();
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
		return def.getPackageName();
	}

	@Override
	public void setPackageName(String packageName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getSignatureName()
	{
		return def.getSignatureName();
	}

	@Override
	public String getJavaSourceCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVdmSourceCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNamePostfix(String postfix)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getNamePostfix()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTag(String tag)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getTag()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenericArguments(IInterfaceDefinition... arguments)
	{
		if (arguments != null)
		{
			this.arguments.addAll(Arrays.asList(arguments));
		}
	}

	@Override
	public List<IInterfaceDefinition> getGenericArguments()
	{
		return this.arguments;
	}

	@Override
	public void setGenericArguments(List<IInterfaceDefinition> arguments)
	{
		if (arguments != null)
		{
			this.arguments.addAll(arguments);
		}
	}

	@Override
	public void setAnnotation(String annotation)
	{
		
	}

}
