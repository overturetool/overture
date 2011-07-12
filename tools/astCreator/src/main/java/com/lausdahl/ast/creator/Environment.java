package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;

public class Environment
{
	public static final IInterfaceDefinition voidDef = new PredefinedClassDefinition("", "void");
	public static final IInterfaceDefinition A = new PredefinedClassDefinition("", "A",true);
	public static final IInterfaceDefinition Q = new PredefinedClassDefinition("", "Q",true);
	public static final IInterfaceDefinition stringDef = new PredefinedClassDefinition("","String");
	public final PredefinedClassDefinition node;
	public final PredefinedClassDefinition token;
	public final PredefinedClassDefinition nodeList;
	public final PredefinedClassDefinition nodeListList;
	public final PredefinedClassDefinition externalNode;
	
	public final String TAG_IAnalysis = "IAnalysis";
	public final String TAG_IAnswer = "IAnswer";
	public final String TAG_IQuestion = "IQuestion";
	public final String TAG_IQuestionAnswer = "IQuestionAnswer";
	
	private final List<ToStringAddOn> toStringAddOn = new Vector<ToStringAddOn>();
	
	public Environment(String defaultPackage)
	{
		// setup env
		node=new PredefinedClassDefinition(defaultPackage, "Node");
		nodeList =new PredefinedClassDefinition(defaultPackage, "NodeList");
		nodeListList =new PredefinedClassDefinition(defaultPackage, "NodeListList");
		token =new PredefinedClassDefinition(defaultPackage, "Token");
		externalNode=new PredefinedClassDefinition(defaultPackage, "ExternalNode");
		// setup env
		addClass(node);
		addClass(nodeList);
		addClass(nodeListList);
		addClass(token);
		addClass(externalNode);
	}
	private final List<IClassDefinition> classes = new Vector<IClassDefinition>();
	private final List<IInterfaceDefinition> interfaces = new Vector<IInterfaceDefinition>();

	public void addClass(IClassDefinition cl)
	{
		for (IClassDefinition def : classes)
		{
			if(def.getName().equals(cl.getName()))
			{
				String msg = "Trying to add a dublicate for class: "+def.getPackageName()+"."+def.getName();
				System.err.println(msg);
				throw new Error(msg);
			}
		}
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
				&& selectedClass.getSuperDef().equals(superClass);
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
	
	public List<IInterfaceDefinition> getAllDefinitions()
	{
		List<IInterfaceDefinition> defs = new Vector<IInterfaceDefinition>();
		defs.addAll(classes);
		defs.addAll(interfaces);
		return defs;
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
		for (IInterfaceDefinition cl : interfaces)
		{
			sb.append("interface " + cl.getName() + "\n");

		}
		for (IClassDefinition cl : classes)
		{
			sb.append("class " + pad(cl.getName(),30)+cl.getPackageName() + "\n");

		}
		return sb.toString();
	}
	private String pad(String text, int length)
	{
		while(text.length()<length)
		{
			text+=" ";
		}
		return text;
	}

	public IInterfaceDefinition getTaggedDef(String tag)
	{
		for (IInterfaceDefinition def : interfaces)
		{
			if(def.getTag().equals(tag))
				
			{
				return def;
			}
		}
		return null;
	}
	
	
	
	
	public void addToStringAddOn(ToStringAddOn addon)
	{
		toStringAddOn.add(addon);
	}



	public List<ToStringAddOn> getToStringAddOns()
	{
		return this.toStringAddOn;
	}
}
