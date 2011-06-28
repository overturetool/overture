package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

public class Environment
{
	private final List<IClassDefinition> classes = new Vector<IClassDefinition>();
	private final List<IInterfaceDefinition> interfaces = new Vector<IInterfaceDefinition>();

	public void addClass(IClassDefinition cl)
	{
		this.classes.add(cl);
	}
	
	public void addInterface(IInterfaceDefinition cl)
	{
		this.interfaces.add(cl);
	}

	public IClassDefinition lookUp(String name)
	{
		for (IClassDefinition cl : classes)
		{
			if (isClassNamesEqual(cl.getName(),name))
			{
				return cl;
			}
		}
		return null;
	}
	
	public IInterfaceDefinition lookUpInterface(String name)
	{
		for (IInterfaceDefinition cl : interfaces)
		{
			if (isClassNamesEqual(cl.getName(),name))
			{
				return cl;
			}
		}
		return null;
	}
	
	private boolean isClassNamesEqual(String a, String b)
	{
		if(a.contains("<"))
		{
			a= a.substring(0,a.indexOf('<'));
		}
		if(b.contains("<"))
		{
			b= b.substring(0,b.indexOf('<'));
		}
		return a.equals(b);
	}

	public boolean isSuperTo(IClassDefinition superClass,
			IClassDefinition selectedClass)
	{
		return !selectedClass.equals(superClass) && selectedClass.hasSuper()
				&& selectedClass.getSuperName().equals(superClass.getName());
	}

	public IClassDefinition getSuperClass(IClassDefinition selectedClass)
	{
		for (IClassDefinition cl : classes)
		{

			if (isSuperTo(cl, selectedClass))
			{
				return cl;
			}
		}
		return null;
	}

	public List<IClassDefinition> getClasses()
	{
		return this.classes;
	}

	public List<IClassDefinition> getSubClasses(IClassDefinition c)
	{
		List<IClassDefinition> subclasses = new Vector<IClassDefinition>();
		for (IClassDefinition sub : classes)
		{
			// if (sub.getSuperClassDefinition() != null
			// && sub.getSuperClassDefinition().equals(c))
			// {
			if (isSuperTo(c, sub))
			{
				subclasses.add(sub);
			}
		}
		return subclasses;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (IClassDefinition cl : classes)
		{
			sb.append("class " + cl.getName() + "\n");

		}
		return sb.toString();
	}
}
