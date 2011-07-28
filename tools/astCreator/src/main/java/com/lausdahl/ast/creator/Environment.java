package com.lausdahl.ast.creator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.PredefinedClassDefinition;

public class Environment
{
	public static final IInterfaceDefinition voidDef = new PredefinedClassDefinition("", "void",true);
	public static final IInterfaceDefinition A = new PredefinedClassDefinition("", "A",true);
	public static final IInterfaceDefinition Q = new PredefinedClassDefinition("", "Q",true);
	public static final IInterfaceDefinition stringDef = new PredefinedClassDefinition("","String",true);
	
	public static final IInterfaceDefinition listDef = new PredefinedClassDefinition("java.util","List",true);
	public static final IInterfaceDefinition vectorDef = new PredefinedClassDefinition("java.util","Vector",true);
	public static final IInterfaceDefinition linkedListDef = new PredefinedClassDefinition("java.util","LinkedList",true);
	public static final IInterfaceDefinition collectionDef = new PredefinedClassDefinition("java.util","Collection",true);
		
	public final PredefinedClassDefinition node;
	public final PredefinedClassDefinition token;
	public final PredefinedClassDefinition nodeList;
	public final PredefinedClassDefinition nodeListList;
	public final PredefinedClassDefinition graphNodeList;
	public final PredefinedClassDefinition graphNodeListList;
	public final PredefinedClassDefinition externalNode;
	
	public final String TAG_IAnalysis = "IAnalysis";
	public final String TAG_IAnswer = "IAnswer";
	public final String TAG_IQuestion = "IQuestion";
	public final String TAG_IQuestionAnswer = "IQuestionAnswer";
	
	private final List<ToStringAddOn> toStringAddOn = new Vector<ToStringAddOn>();
	
	private String defaultPackage = "org.overture.ast.node";
	private String analysisPackage = "org.overture.ast.analysis";
	
	public Environment()
	{
//		this.defaultPackage = defaultPackage;
		// setup env
		node=new PredefinedClassDefinition(defaultPackage, "Node");
		nodeList =new PredefinedClassDefinition(defaultPackage, "NodeList");
		nodeListList =new PredefinedClassDefinition(defaultPackage, "NodeListList");
		graphNodeList =new PredefinedClassDefinition(defaultPackage, "GraphNodeList");
		graphNodeListList =new PredefinedClassDefinition(defaultPackage, "GraphNodeListList");
		token =new PredefinedClassDefinition(defaultPackage, "Token");
		externalNode=new PredefinedClassDefinition(defaultPackage, "ExternalNode");
		// setup env
		addClass(node);
		addClass(nodeList);
		addClass(nodeListList);
		addClass(token);
		addClass(externalNode);
	}
	
	public void setDefaultPackages(String defaultPackages)
	{
		String oldPackage = this.defaultPackage;
		this.defaultPackage = defaultPackages;
		node.setPackageName(defaultPackage);
		nodeList.setPackageName(defaultPackage);
		nodeListList.setPackageName(defaultPackage);
		graphNodeList.setPackageName(defaultPackage);
		graphNodeListList.setPackageName(defaultPackage);
		token.setPackageName(defaultPackage);
		externalNode.setPackageName(defaultPackage);
		
		for (IClassDefinition c : classes)
		{
			if(c.getPackageName().equals(oldPackage))
			{
				c.setPackageName(defaultPackage);
			}
		}
		
		for (IInterfaceDefinition c : interfaces)
		{
			if(c.getPackageName().equals(oldPackage))
			{
				c.setPackageName(defaultPackage);
			}
		}
	}
	
	public void setAnalysisPackages(String analysisPackage)
	{
		String oldPackage = this.analysisPackage;
		this.analysisPackage = analysisPackage;
		for (IClassDefinition c : classes)
		{
			if(c.getPackageName().equals(oldPackage))
			{
				c.setPackageName(analysisPackage);
			}
		}
		
		for (IInterfaceDefinition c : interfaces)
		{
			if(c.getPackageName().equals(oldPackage))
			{
				c.setPackageName(analysisPackage);
			}
		}
	}
	
	public String getDefaultPackage()
	{
		return this.defaultPackage;
	}
	
	public String getAnalysisPackage()
	{
		return this.analysisPackage;
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
	
	
	public Environment extendWith(Environment env)
	{
		setDefaultPackages(env.getDefaultPackage());
		setAnalysisPackages(env.getAnalysisPackage());
		List<IClassDefinition> extendedClasses = new Vector<IClassDefinition>();
		//not matched classes
		List<IClassDefinition> notMatchedClasses = new Vector<IClassDefinition>();
		notMatchedClasses.addAll(classes);
		
		
		//first extend all existing classes
		for (IClassDefinition c : classes)
		{
			for (IClassDefinition cNew : env.classes)
			{
				if(c.getName().equals(cNew.getName()))
				{
					extendWith(this,c,cNew);
					extendedClasses.add(cNew);
					notMatchedClasses.remove(c);
					break;
				}
			}
		}
		
		//set package for all alternatives not matched
		for (IClassDefinition c : notMatchedClasses)
		{
			if(c instanceof CommonTreeClassDefinition )
			{
				switch (((CommonTreeClassDefinition) c).getType())
				{
					case Alternative:
						c.setPackageName(c.getSuperDef().getPackageName());
						break;
					case Custom:
						break;
					case Production:
						break;
					case SubProduction:
						break;
					case Token:
						c.setPackageName(defaultPackage+".tokens");
						break;
					case Unknown:
						break;
					
				}
				
			}
		}
		
		//add new classes
		List<IClassDefinition> newClasses = new Vector<IClassDefinition>();
		newClasses.addAll(env.classes);
		newClasses.removeAll(extendedClasses);
		
		
		for (IClassDefinition def : newClasses)
		{
			if(def instanceof CommonTreeClassDefinition && def.getSuperDef()!=null && def.getSuperDef() instanceof CommonTreeClassDefinition)
			{
				IClassDefinition oldSuper = def.getSuperDef();
				((CommonTreeClassDefinition)def).imports.remove(oldSuper);
				((CommonTreeClassDefinition)def).setSuperClass(this.lookUp(oldSuper.getName()));
				def.updateEnvironment(this);
			}
		}
		this.classes.addAll(newClasses);
		
		//for new we skip the interfaces
		
		//update env for all
		for (IClassDefinition def : classes)
		{
			def.updateEnvironment(this);
		}
		
		return this;
	}

	private void extendWith(Environment env,IClassDefinition c, IClassDefinition cNew)
	{
		c.setPackageName(cNew.getPackageName());
		
		List<Field> fields = new ArrayList<Field>(c.getFields());
		List<Field> matchedFields = new Vector<Field>();
		for (Field f : fields)
		{
			for (Field newF : cNew.getFields())
			{
				if(f.getName().equals(newF.getName()))
				{
					f.setType(newF.getUnresolvedType());
					matchedFields.add(newF);
				}
			}
		}
		
		
		List<Field> newFields = new Vector<Field>();
		newFields.addAll(cNew.getFields());
		newFields.removeAll(matchedFields);
		for (Field field : newFields)
		{
			field.updateEnvironment(env);
			c.addField(field);
		}
		
//		//tryout
//		for (Field field : c.getFields())
//		{
//			field.updateEnvironment(env);
//		}
	}


}
