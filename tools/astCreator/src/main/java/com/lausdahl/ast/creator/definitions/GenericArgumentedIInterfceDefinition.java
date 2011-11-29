package com.lausdahl.ast.creator.definitions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.Method;

public class GenericArgumentedIInterfceDefinition implements
		IInterfaceDefinition
{
	IInterfaceDefinition def;
	List<String> arguments = new Vector<String>();

	public GenericArgumentedIInterfceDefinition(IInterfaceDefinition def,
			String... arguments)
	{
		this.def = def;
		setGenericArguments(Arrays.asList(arguments));
	}

	public JavaName getName()
	{
		return this.def.getName();
	}

	public Set<String> getImports()
	{
		return def.getImports();
	}

	public String getJavaSourceCode(StringBuilder sb)
	{
		return null;
	}

	public String getVdmSourceCode(StringBuilder sb)
	{
		return null;
	}

	public void setTag(String tag)
	{
	}

	public String getTag()
	{
		return null;
	}

	public List<String> getGenericArguments()
	{
		return this.arguments;
	}

	public void setGenericArguments(List<String> arguments)
	{
		if (arguments != null)
		{
			this.arguments.addAll(arguments);
		}
	}

	public void setAnnotation(String annotation)
	{

	}

	public List<Method> getMethods()
	{
		return null;
	}

	public Set<Method> getMethod(String name)
	{
		return null;
	}

	public void addMethod(Method m)
	{

	}

	public String getGenericsString()
	{
		StringBuilder sb = new StringBuilder();
		if (!this.arguments.isEmpty())
		{
			sb.append("<");
			for (Iterator<String> itr = this.arguments.iterator(); itr.hasNext();)
			{
				String type = itr.next();
				sb.append(type);
				if (itr.hasNext())
				{
					sb.append(", ");
				}
			}
			sb.append(">");
		}
		return sb.toString();
	}

	public void setFinal(boolean isFinal)
	{

	}

	public void setAbstract(boolean isAbstract)
	{

	}

	public boolean isFinal()
	{
		return false;
	}

	public boolean isAbstract()
	{
		return false;
	}

	public Set<IInterfaceDefinition> getSuperDefs()
	{
		return new HashSet<IInterfaceDefinition>();
	}

}
