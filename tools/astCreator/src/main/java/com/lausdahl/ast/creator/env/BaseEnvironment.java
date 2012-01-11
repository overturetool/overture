package com.lausdahl.ast.creator.env;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;

public class BaseEnvironment
{
	public static final IInterfaceDefinition voidDef = new PredefinedClassDefinition("", "void", true);
	public static final IInterfaceDefinition stringDef = new PredefinedClassDefinition("", "String", true);

	public static final IInterfaceDefinition serializableDef = new PredefinedClassDefinition("java.io", "Serializable", true);
	public static final IInterfaceDefinition listDef = new PredefinedClassDefinition("java.util", "List", true);
	
	public static final IInterfaceDefinition queueDef = new PredefinedClassDefinition("java.util", "Queue", true);
	
	public static final IInterfaceDefinition vectorDef = new PredefinedClassDefinition("java.util", "Vector", true);
	public static final IInterfaceDefinition linkedListDef = new PredefinedClassDefinition("java.util", "LinkedList", true);
	public static final IInterfaceDefinition collectionDef = new PredefinedClassDefinition("java.util", "Collection", true);

	public final PredefinedClassDefinition node;
	public final PredefinedClassDefinition token;
	public final PredefinedClassDefinition nodeList;
	public final PredefinedClassDefinition nodeListList;
	public final PredefinedClassDefinition graphNodeList;
	public final PredefinedClassDefinition graphNodeListList;
	public final PredefinedClassDefinition externalNode;

	protected String name;
	protected String defaultPackage = "org.overture.ast.node";
	
	protected final Set<IInterfaceDefinition> baseClasses = new HashSet<IInterfaceDefinition>();
	protected final List<IClassDefinition> classes = new Vector<IClassDefinition>();
	protected final List<IInterfaceDefinition> interfaces = new Vector<IInterfaceDefinition>();

	
	public BaseEnvironment(String name)
	{
		this.name =name;
		node = new PredefinedClassDefinition(defaultPackage, "Node");
		nodeList = new PredefinedClassDefinition(defaultPackage, "NodeList");
		nodeListList = new PredefinedClassDefinition(defaultPackage, "NodeListList");
		graphNodeList = new PredefinedClassDefinition(defaultPackage, "GraphNodeList");
		graphNodeListList = new PredefinedClassDefinition(defaultPackage, "GraphNodeListList");
		token = new PredefinedClassDefinition(defaultPackage, "Token");
		externalNode = new PredefinedClassDefinition(defaultPackage, "ExternalNode");
		// setup env
		addClass(node);
		addClass(nodeList);
		addClass(nodeListList);
		addClass(token);
		addClass(externalNode);

		baseClasses.addAll(classes);
		interfaces.add(serializableDef);
		interfaces.add(listDef);
		interfaces.add(queueDef);
		interfaces.add(linkedListDef);
		interfaces.add(collectionDef);
		baseClasses.add(vectorDef);
		baseClasses.add(linkedListDef);
	}
	
	
	public String getDefaultPackage()
	{
		return this.defaultPackage;
	}
	
	public void addClass(IClassDefinition cl)
	{
		for (IClassDefinition def : classes)
		{
			if (def.getName().equals(cl.getName()))
			{
				String msg = "Trying to add a dublicate for class: "
						+ def.getName().getCanonicalName();
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
	
	public void setDefaultPackages(String defaultPackages)
	{
		String oldPackage = this.defaultPackage;
		this.defaultPackage = defaultPackages;
		node.getName().setPackageName(defaultPackage);
		nodeList.getName().setPackageName(defaultPackage);
		nodeListList.getName().setPackageName(defaultPackage);
		graphNodeList.getName().setPackageName(defaultPackage);
		graphNodeListList.getName().setPackageName(defaultPackage);
		token.getName().setPackageName(defaultPackage);
		externalNode.getName().setPackageName(defaultPackage);

		for (IClassDefinition c : classes)
		{
			if (c.getName().getPackageName().equals(oldPackage))
			{
				c.getName().setPackageName(defaultPackage);
			}
		}

		for (IInterfaceDefinition c : interfaces)
		{
			if (c.getName().getPackageName().equals(oldPackage))
			{
				c.getName().setPackageName(defaultPackage);
			}
		}
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("* \n* "+name+"\n*\n");
		for (IInterfaceDefinition cl : interfaces)
		{
			sb.append("interface " + cl.getName() + "\n");

		}
		for (IClassDefinition cl : classes)
		{
			sb.append("class " + pad(cl.getName().getName(), 30)
					+ cl.getName().getPackageName() + "\n");

		}
		return sb.toString();
	}
	
	protected String pad(String text, int length)
	{
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}
}
