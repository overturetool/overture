package com.lausdahl.ast.creator.definitions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.methods.Method;

public class InterfaceDefinition implements IInterfaceDefinition
{
	public List<Method> methods = new Vector<Method>();
	public Set<IInterfaceDefinition> imports = new HashSet<IInterfaceDefinition>();
	List<IInterfaceDefinition> genericArguments = new Vector<IInterfaceDefinition>();
	public List<String> supers = new Vector<String>();
	protected String name;
	protected String namePostfix = "";
	// public String superName;
	private String packageName = "";
	public static boolean VDM = false;
	private String tag = "";
	protected String namePrefix = "I";
	protected String annotation = "";

	public InterfaceDefinition(String name)
	{
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getName()
	 */
	
	public String getName()
	{
		String tmp = namePrefix + this.name;
		if (tmp.contains("<"))
		{
			tmp = tmp.replace("<", namePostfix + "<");
		} else if (genericArguments.isEmpty())
		{
			tmp += namePostfix;
		} else
		{
			String tmp1 = tmp + namePostfix + "<";
			for (IInterfaceDefinition arg : genericArguments)
			{
				tmp1 += arg.getSignatureName() + ", ";
			}
			if (!genericArguments.isEmpty())
			{
				tmp1 = tmp1.substring(0, tmp1.length() - 2);
			}
			tmp = tmp1 + ">";
		}
		return tmp;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getImports()
	 */
	
	public Set<String> getImports()
	{
		Set<String> imports = new HashSet<String>();

		for (IInterfaceDefinition i : this.imports)
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

		return imports;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#isFinal()
	 */
	
	public boolean isFinal()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#isAbstract()
	 */
	
	public boolean isAbstract()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getPackageName()
	 */
	
	public String getPackageName()
	{
		return this.packageName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#setPackageName(java.lang.String)
	 */
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		try{
		return getJavaSourceCode(sb);
		}catch(Throwable e)
		{
			
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getSignatureName()
	 */
	
	public String getSignatureName()
	{
		String n = getName();
		if (n.contains("<"))
		{
			return n.substring(0, n.indexOf('<'));
		}
		return n;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getJavaSourceCode()
	 */
	
	public String getJavaSourceCode(StringBuilder sb)
	{
		

		sb.append(IClassDefinition.classHeader + "\n");

		if (getPackageName() != null)
		{
			sb.append("\npackage " + getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports())
		{
			sb.append("import " + importName + ";\n");
		}

		sb.append("\n\npublic interface " + getName());

		if (!supers.isEmpty())
		{
			sb.append(" extends ");
			StringBuilder intfs = new StringBuilder();
			for (String intfName : supers)
			{
				intfs.append(intfName + ", ");
			}
			sb.append(intfs.subSequence(0, intfs.length() - 2));
		}

		sb.append("\n{");

		// String tmp = IClassDefinition.classHeader
		// + "\n\npackage generated.node;\n\n\n";
		//
		// tmp += "public " + "interface " + name;
		//
		// tmp += "\n{\n\n";

		for (Method m : methods)
		{
			sb.append(m.getJavaDoc() + "\n");
			sb.append(m.getSignature() + ";\n");
		}

		sb.append("\n}\n");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.lausdahl.ast.creator.IInterfaceDefinition#getVdmSourceCode()
	 */
	
	public String getVdmSourceCode(StringBuilder sb)
	{
		sb.append(IClassDefinition.classHeader + "\n");

		if (getPackageName() != null)
		{
			sb.append("\n--package " + getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports())
		{
			sb.append("--import " + importName + ";\n");
		}

		if (annotation != null && annotation.length() > 0)
		{
			sb.append(annotation + "\n");
		}
		sb.append("/*public interface*/class " + getSignatureName());

		if (!supers.isEmpty())
		{
			sb.append(" is subclass of ");
			StringBuilder intfs = new StringBuilder();
			for (String intfName : supers)
			{
				intfs.append(intfName + ", ");
			}
			sb.append(intfs.subSequence(0, intfs.length() - 2));
		}

		sb.append("\ntypes\n\n");
		for (String t : getGenericClassArguments())
		{
			sb.append("\n\tpublic " + t + " = ?;\n");
		}

		sb.append("\noperations\n\n");

		// String tmp = IClassDefinition.classHeader
		// + "\n\npackage generated.node;\n\n\n";
		//
		// tmp += "public " + "interface " + name;
		//
		// tmp += "\n{\n\n";

		for (Method m : methods)
		{
			sb.append(m.getJavaDoc() + "\n");
			sb.append(m.getVdmSignature() + "is subclass responsibility"
					+ ";\n");
		}

		sb.append("\nend " + getSignatureName());
		return sb.toString().replace("org.overturetool.vdmj.lex.", "").replace("OrgOverturetoolVdmjLex", "");
	}

	protected List<String> getGenericClassArguments()
	{
		List<String> args = new Vector<String>();

		if (getName().contains("<"))
		{
			String tmp = getName().substring(getName().indexOf('<') + 1, getName().indexOf('>'));
			for (String string : tmp.split(","))
			{
				args.add(string);

			}
		}
		return args;
	}

	public static String firstLetterUpper(String name)
	{
		return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
	}

	public static String javaClassName(String name)
	{

		while (name.indexOf(Field.fieldPrefic) != -1)
		{
			int index = name.indexOf(Field.fieldPrefic);
			name = name.substring(0, index)
					+ firstLetterUpper(name.substring(index
							+ Field.fieldPrefic.length()));
		}

		while (name.indexOf('_') != -1)
		{
			int index = name.indexOf('_');
			name = name.substring(0, index)
					+ firstLetterUpper(name.substring(index + 1));
		}

		// if(InterfaceDefinition.VDM && name.contains("."))
		// {
		// return name.substring(name.lastIndexOf('.')+1);
		// }

		if (name.contains("."))
		{
			String[] arr = name.split("\\.");
			name = "";
			for (String string : arr)
			{
				name += firstLetterUpper(string);
			}
		}

		return name;
	}

	public void setNamePostfix(String postfix)
	{
		this.namePostfix = postfix;
	}

	
	public String getNamePostfix()
	{
		return namePostfix;
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
		if (arguments != null)
		{
			this.genericArguments.addAll(Arrays.asList(arguments));
		}
	}

	public void setGenericArguments(List<IInterfaceDefinition> arguments)
	{
		if (arguments != null)
		{
			this.genericArguments.addAll(arguments);
		}
	}

	
	public List<IInterfaceDefinition> getGenericArguments()
	{
		return this.genericArguments;
	}

	
	public void setAnnotation(String annotation)
	{
		this.annotation = annotation;
	}

	public String getImportName()
	{
		return getPackageName() + "." + getSignatureName();
	}
}
