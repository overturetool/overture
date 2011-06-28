package com.lausdahl.ast.creator;

import java.util.List;
import java.util.Vector;

public class Field
{
	public boolean isTokenField = false;
	public boolean isAspect = false;
	public String name;
	public String type;
	public boolean isList = false;
	public static String fieldPrefic = "_";
	private Environment env;

	public Field(Environment env)
	{
		this.env = env;
	}

	public List<String> getRequiredImports()
	{
		List<String> imports = new Vector<String>();
//		imports.add("java.util.List");
		
		IInterfaceDefinition defIntf = env.lookUpInterface(getType());
		if (defIntf != null )
		{
			imports.add(defIntf.getPackageName() + "." + defIntf.getSignatureName());
		}
 
		IClassDefinition def = env.lookUp(getType());
		if (def != null )
		{
			imports.add(def.getPackageName() + "." + def.getSignatureName());
		}

		return imports;
	}

	@Override
	public String toString()
	{
		return getName() + ": " + getType();
	}

	public String getName()
	{
		String tmp = (name == null ? type : name);
		return fieldPrefic + tmp.substring(0, 1).toLowerCase()
				+ tmp.substring(1);
	}

	public String getType()
	{
		String internaalType = getInternalType();
		if (isList)
		{

			internaalType = "NodeList<" + internaalType + ">";
		}

		// String tmp = internaalType;
		// if(InterfaceDefinition.VDM && tmp.contains("."))
		// {
		// return tmp.substring(tmp.lastIndexOf('.')+1);
		// }else
		// {
		// return tmp;
		// }

		return internaalType;
	}

	public String getMethodArgumentType()
	{
		String internaalType = getInternalType();
		if (isList)
		{

			return "List<? extends " + internaalType + ">";
		}
		return internaalType;
	}

	protected String getInternalType()
	{
		if (isTokenField)
		{
			return type;
		}

		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				if (c.getType() == CommonTreeClassDefinition.ClassType.Token
						&& c.thisClass.getText().equals(type))
				{
					return c.getName();
				}
			}
		}

		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				if (c.thisClass.getText().equals(type))
				{
					return c.getName();
				}
			}
		}

		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CustomClassDefinition)
			{
				CustomClassDefinition c = (CustomClassDefinition) cd;

				if (c.name.equals(type))
				{
					return c.name;
				}
			}
		}

		// if(type.equals(Double.class.getSimpleName()))
		// {
		// return type;
		// }
		// if(type.equals(Integer.class.getSimpleName()))
		// {
		// return type;
		// }
		// if(type.equals(String.class.getSimpleName()))
		// {
		// return type;
		// }
		return "%" + type;
	}

}
