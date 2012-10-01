package com.lausdahl.ast.creator.definitions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.ToStringAddOn;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;
import com.lausdahl.ast.creator.methods.Method;

public class BaseClassDefinition extends InterfaceDefinition implements
		IClassDefinition
{ 
	protected final List<Field> fields = new Vector<Field>();
	protected final List<ToStringAddOn> toStringAddOn = new Vector<ToStringAddOn>();
	protected final Set<IInterfaceDefinition> interfaces = new HashSet<IInterfaceDefinition>();
	protected IClassDefinition superDef;

	public BaseClassDefinition(JavaName name)
	{
		super(name);
	}

	public boolean hasSuper()
	{ 
		return this.superDef != null;
	}

	public void setSuper(IClassDefinition newSuper)
	{
		this.superDef = newSuper;
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
	public Set<String> getImports(Environment env)
	{
		Set<String> imports = new HashSet<String>();

		if (getSuperDef() != null)
		{
			imports.add(getSuperDef().getName().getCanonicalName());
		}

		for (IInterfaceDefinition i : this.imports)
		{
			String theImport = i.getName().getCanonicalName(); 
			imports.add(theImport);
		}

		for (IInterfaceDefinition i : this.interfaces)
		{
			imports.add(i.getName().getCanonicalName());
		}
		for (Method m : methods)
		{
			for (String string : m.getRequiredImports(env))
			{
				imports.add(string);
			}
		}

		for (Field m : fields)
		{
			for (String string : m.getRequiredImports(env))
			{
				imports.add(string);
			}
		}

		return imports;
	}

	public String getJavaSourceCode(StringBuilder sb, Environment env)
	{
		sb.append(IInterfaceDefinition.copurightHeader + "\n");
		sb.append(IClassDefinition.classHeader + "\n");

		if (getName().getPackageName() != null)
		{
			sb.append("\npackage " + getName().getPackageName() + ";\n\n\n");
		}

		for (String importName : getImports(env))
		{
			//importName = importName.replace("org.overture.ast", "eu.compassresearch.ast");
			sb.append("import " + importName + ";\n");
		}
		sb.append("\n\n");
		sb.append(javaDoc);

		if (annotation != null && annotation.length() > 0)
		{
			sb.append(annotation + "\n");
		}
		sb.append("public " + (isFinal() ? "final " : "")
				+ (isAbstract() ? "abstract " : "") + "class "
				+ getName().getName());

		sb.append(getGenericsString());

		if (hasSuper())
		{
			sb.append(" extends " + getSuperDef().getName().getName()
					+ getSuperDef().getGenericsString());

		}

		if (!interfaces.isEmpty())
		{
			sb.append(" implements ");
			// StringBuilder intfs = new StringBuilder();
			// for (IInterfaceDefinition intfName : interfaces)
			// {
			// intfs.append(intfName.getName().getName()
			// + intfName.getGenericsString() + ", ");
			// }
			// sb.append(intfs.subSequence(0, intfs.length() - 2));
			for (Iterator<IInterfaceDefinition> iterator = interfaces.iterator(); iterator.hasNext();)
			{
				IInterfaceDefinition intf = iterator.next();
				sb.append(intf.getName() + intf.getGenericsString());
				if (iterator.hasNext())
				{
					sb.append(", ");
				}

			}
		}

		sb.append("\n{");
		sb.append("\n\tprivate static final long serialVersionUID = 1L;\n");
		for (Field f : fields)
		{
			if (isRefinedField(f, env))
			{
				continue;
			}
			if (f.structureType == StructureType.Graph)
			{
				sb.append("\n\t/**\n\t* Graph field, parent will not be removed when added and parent \n\t*  of this field may not be this node. Also excluded for visitor.\n\t*/");
			}

			sb.append("\n\t" + f.accessspecifier.syntax + " " + f.getType(env)
					+ " " + f.getName(env));
			if (f.isList)
			{
				if (f.isTypeExternalNotNode())
				{
					sb.append(" = new Vector<" + f.type.getName().getName()
							+ ">()");
				} else
				{
					sb.append(" = new " + f.getType(env) + "(this)");
				}
			}
			
			if(f.hasCustomInitializer())
			{
				sb.append(" = "+f.getCustomInitializer());
			}
			sb.append(";");
		}

		sb.append("\n\n");

		StringBuffer noneCtorMethods = new StringBuffer();
		for (Method m : methods)
		{
			if (m.isConstructor)
			{
				sb.append(m.getJavaSourceCode(env) + "\n");
			} else
			{
				noneCtorMethods.append(m.getJavaSourceCode(env) + "\n");
			}
		}

		sb.append(noneCtorMethods);
		sb.append("\n}\n");
		return sb.toString();
	}


	public String getVdmSourceCode(StringBuilder sb)
	{
		return "";
	}

	// public static String firstLetterUpper(String name)
	// {
	// return String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1);
	// }

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

	public boolean hasField(String name, Environment env)
	{
		if (getSuperDef() != null && getSuperDef().hasField(name, env))
		{
			return true;
		}
		for (Field f : getFields())
		{
			if (f.getName(env).equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public boolean refinesField(String name, Environment env)
	{
		boolean fieldExistsInSuper = false;
		if (getSuperDef() != null && getSuperDef().hasField(name,env))
		{
			fieldExistsInSuper = true;
		}
		for (Field f : getFields())
		{
			if (f.getName(env).equals(name))
			{
				return fieldExistsInSuper;
			}
		}
		return false;
	}

	public boolean isRefinedField(Field field, Environment env)
	{
		boolean existsInSuper = (getSuperDef() != null && getSuperDef().hasField(field.getName(env),env));
		for (Field f : getFields())
		{
			if (f == field && existsInSuper)
			{
				return true;
			}
		}
		return false;
	}

	public List<Field> getInheritedFields()
	{
		List<Field> fields = new Vector<Field>();
		IClassDefinition sDef = getSuperDef();
		if (sDef != null)
		{
			fields.addAll(sDef.getInheritedFields());
			fields.addAll(sDef.getFields());
		}
		return fields;
	}

	public void checkFieldTypeHierarchy(Environment env) throws AstCreatorException
	{
		for (Field field : getFields())
		{
			if (isRefinedField(field,env))
			{
				Field superField = null;
				for (Field iField : getInheritedFields())
				{
					if (field.getName(env).equals(iField.getName(env)))
					{
						superField = iField;
						break;
					}
				}

				if (!isSubclassOf(field.type, superField.type))
				{
					String msg = "Field \"" + field.getName(env) + "\" in class "
							+ getName().getName() + " with type \""
							+ field.getType(env) + "\" is not a subclass of \""
							+ superField.getType(env) + "\"";
					throw new AstCreatorException(msg, null, true);
				} else if(field.type!=superField.type)
				{
					String msg = "Field \"" + field.getName(env) + "\" in class "
							+ getName().getName() + " with type \""
							+ field.getType(env) + "\" specializes \""
							+ superField.getType(env) + "\"";
					System.out.println("WARNING: " + msg);
				}
			}
		}
	}

	private static boolean isSubclassOf(IInterfaceDefinition subclass,
			IInterfaceDefinition superClass)
	{
		if (subclass == superClass)
		{
			return true;
		}

		// TODO: this check is not yet for recursice interfaces
		if (subclass instanceof IClassDefinition
				&& ((IClassDefinition) subclass).getInterfaces().contains(superClass))
		{
			return true;
		}

		if (subclass instanceof IClassDefinition
				&& ((IClassDefinition) subclass).getSuperDef() != null
				&& isSubclassOf(((IClassDefinition) subclass).getSuperDef(), superClass))
		{
			return true;
		}
		return false;
	}


	public void addInterface(IInterfaceDefinition intf)
	{
		for (IInterfaceDefinition i : interfaces)
		{
			if (i.getName().getName().equals(intf.getName().getName()))
			{
				return;
			}
		}
		this.interfaces.add(intf);
	}

}
