package com.lausdahl.ast.creator.definitions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.ToStringAddOn;
import com.lausdahl.ast.creator.methods.Method;

public class BaseClassDefinition extends InterfaceDefinition implements
		IClassDefinition
{
	protected List<Field> fields = new Vector<Field>();
	protected List<ToStringAddOn> toStringAddOn = new Vector<ToStringAddOn>();
	public Set<IInterfaceDefinition> interfaces = new HashSet<IInterfaceDefinition>();
	public IClassDefinition superDef;

	public BaseClassDefinition(String name)
	{
		super(name);
		namePrefix ="";
	}



	public boolean hasSuper()
	{
		return this.superDef != null;
	}

	public void addField(Field field)
	{
		this.fields.add(field);
	}

	public List<Field> getFields()
	{
		return this.fields;
	}

	@Override
	public Set<String> getImports()
	{
		Set<String> imports = new HashSet<String>();

		if (getSuperDef() != null)
		{
			String n = getSuperDef().getPackageName() + "."
					+ getSuperDef().getSignatureName();
			
				imports.add(n);
		}

		for (IInterfaceDefinition i : this.imports)
		{
			String n = i.getPackageName() + "." + i.getSignatureName();
				imports.add(n);
		}
		
		for (IInterfaceDefinition i : this.interfaces)
		{
			String n = i.getPackageName() + "." + i.getSignatureName();
				imports.add(n);
		}
		// imports.addAll(this.imports);
		for (Method m : methods)
		{
			for (String string : m.getRequiredImports())
			{
					imports.add(string);
			}
		}

		for (Field m : fields)
		{
			for (String string : m.getRequiredImports())
			{
				imports.add(string);
			}
		}

		return imports;
	}

	@Override
	public String toString()
	{
		return getJavaSourceCode();
	}

	public String getJavaSourceCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(IClassDefinition.classHeader + "\n");

		if (getPackageName() != null)
		{
			sb.append("\npackage " + getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports())
		{
			sb.append("import " + importName + ";\n");
		}
		sb.append("\n\n");
		
		if (annotation != null && annotation.length() > 0)
		{
			sb.append(annotation + "\n");
		}
		sb.append("public " + (isFinal() ? "final " : "")
				+ (isAbstract() ? "abstract " : "") + "class " + getName());

		if (hasSuper())
		{
			sb.append(" extends " + getSuperDef().getName());
		}

		if (!interfaces.isEmpty())
		{
			sb.append(" implements ");
			StringBuilder intfs = new StringBuilder();
			for (IInterfaceDefinition intfName : interfaces)
			{
				intfs.append(intfName.getName() + ", ");
			}
			sb.append(intfs.subSequence(0, intfs.length() - 2));
		}

		sb.append("\n{");

		for (Field f : fields)
		{
			sb.append("\n\t"+f.accessspecifier.syntax+" " + f.getType() + " " + f.getName() );
			if(f.isList)
			{
				if(f.isTypeExternalNotNode())
				{
					sb.append(" = new Vector<"+f.type.getSignatureName()+">()");
				}else
				{
				sb.append(" = new "+ f.getType()+"(this)");
				}
			}
			sb.append(";");
		}

		sb.append("\n\n");

		StringBuffer noneCtorMethods = new StringBuffer();
		for (Method m : methods)
		{
			if (m.isConstructor)
			{
				sb.append(m.getJavaSourceCode() + "\n");
			}else
			{
				noneCtorMethods.append(m.getJavaSourceCode() + "\n");
			}
		}
		
		sb.append(noneCtorMethods);

//		for (Method m : methods)
//		{
//			if (!m.isConstructor)
//			{
//				sb.append(m.getJavaSourceCode() + "\n");
//			}
//		}

		sb.append("\n}\n");

		return sb.toString();
	}

	public String getVdmSourceCode()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(IClassDefinition.classHeader + "\n");

		if (getPackageName() != null)
		{
			sb.append("\n--package " + getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports())
		{
			sb.append("--import " + importName + ";\n");
		}

		sb.append("class " + getSignatureName());

		if (hasSuper() || !interfaces.isEmpty())
		{
			sb.append(" is subclass of ");
		}

		if (hasSuper())
		{
			sb.append(getSuperSignatureName());
		}
		if (!interfaces.isEmpty())
		{
			if (hasSuper())
			{
				sb.append(" , ");
			}
			StringBuilder intfs = new StringBuilder();
			for (IInterfaceDefinition intfName : interfaces)
			{
				intfs.append(stripGenericArguments(intfName.getName()) + ", ");
			}
			sb.append(intfs.subSequence(0, intfs.length() - 2));
		}

		sb.append("\ntypes\n");
		sb.append("\n\tpublic String = seq of char;\n");

		for (String t : getGenericClassArguments())
		{
			sb.append("\n\tpublic " + t + " = ?;\n");
		}

		sb.append("\ninstance variables\n");

		for (Field f : fields)
		{
			sb.append("\n\tprivate " + f.getName() + " : ["
					+ stripGenericArguments(f.getType()) + "] := nil;");
		}

		sb.append("\n\noperations\n");

		if (getSignatureName().equals("PExp"))
		{
			sb.append("\n\n\n-- VDMJ interitance PATCH --\n");
			sb.append("public parent : ()  ==> [Node]\n");
			sb.append("parent()== return self.parent_;\n");
			sb.append("public parent : [Node]  ==> ()\n");
			sb.append("parent(p)== parent_:=p;\n");
			sb.append("-- VDMJ interitance PATCH --\n\n\n");
		}

		for (Method m : methods)
		{
			if (m.isConstructor)
			{
				sb.append(m.getVdmSourceCode() + "\n");
			}
		}

		for (Method m : methods)
		{
			if (!m.isConstructor)
			{
				sb.append(m.getVdmSourceCode() + "\n");
			}
		}

		sb.append("\nend " + getSignatureName());

		return sb.toString().replaceAll("this", "self").replaceAll("null", "nil").replace("org.overturetool.vdmj.lex.", "").replace("OrgOverturetoolVdmjLex", "").replaceAll("self\\.", "").replaceAll("token", "token_").replaceAll("super\\.", (getSuperDef() != null ? getSuperDef().getName()
				: "")
				+ "`");
	}

	// public static String javaClassName(String name)
	// {
	// while (name.indexOf('_') != -1)
	// {
	// int index = name.indexOf('_');
	// name = name.substring(0, index)
	// + firstLetterUpper(name.substring(index + 1));
	// }
	// return name;
	// }

	public static String firstLetterUpper(String name)
	{
		return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
	}

	
	public String getSuperSignatureName()
	{
		String n = getSuperDef().getName();
		if (n.contains("<"))
		{
			return n.substring(0, n.indexOf('<'));
		}
		return n;
	}

	public static String stripGenericArguments(String name)
	{
		String n = name;
		if (n.contains("<") && !n.contains("/*"))
		{
			return n.substring(0, n.indexOf('<'));
		}
		return n;
	}

	
	public IClassDefinition getSuperDef()
	{
		return this.superDef;
	}




	public Set<IInterfaceDefinition> getInterfaces()
	{
		return this.interfaces;
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
