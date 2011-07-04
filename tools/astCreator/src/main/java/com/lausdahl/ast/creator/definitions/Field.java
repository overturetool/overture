package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;

public class Field
{
	public static enum AccessSpecifier
	{
		Private("private"), Protected("protected"), Public("public");
		public final String syntax;

		private AccessSpecifier(String syntax)
		{
			this.syntax = syntax;
		}
	}

	public boolean isTokenField = false;
	public boolean isAspect = false;
	public String name;
	public IInterfaceDefinition type;
	public boolean isList = false;
	public static String fieldPrefic = "_";
	private Environment env;
	private String unresolvedType;
	public AccessSpecifier accessspecifier = AccessSpecifier.Private;

	public Field(Environment env)
	{
		this.env = env;
	}

	public List<String> getRequiredImports()
	{
		List<String> imports = new Vector<String>();
		if (isList)
		{
			imports.add("java.util.List");
			imports.add(getInternalType(unresolvedType).getPackageName() + "."
					+ getInternalType(unresolvedType).getSignatureName());
		}
		// imports.add("java.util.List");

		IInterfaceDefinition defIntf = env.lookUpInterface(getType());
		if (defIntf != null)
		{
			imports.add(defIntf.getPackageName() + "."
					+ defIntf.getSignatureName());
		}

		IClassDefinition def = env.lookUp(getType());
		if (def != null)
		{
			imports.add(def.getPackageName() + "." + def.getSignatureName());
		}

		return imports;
	}

	@Override
	public String toString()
	{
		String name = null;
		String typeName = null;
		try
		{
			name = getName();
			typeName = getType();
		} catch (Exception e)
		{

		}
		return name + ": " + typeName;
	}

	public String getName()
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		String tmp = (name == null ? type.getName() : name);
		return fieldPrefic + tmp.substring(0, 1).toLowerCase()
				+ tmp.substring(1);
	}

	public String getType()
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		String internaalType = type.getName();// getInternalType();
		if (isList)
		{

			internaalType = env.nodeList.getSignatureName() + "<"
					+ internaalType + ">";
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
		// String internaalType = getInternalType();
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		String internaalType = type.getName();
		if (isList)
		{

			return "List<? extends " + internaalType + ">";
		}
		return internaalType;
	}

	public String getInnerTypeForList()
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		String internaalType = type.getName();
		// if (isList)
		// {
		//
		// return "List<? extends " + internaalType + ">";
		// }
		return internaalType;
	}

	protected IInterfaceDefinition getInternalType(String unresolvedTypeName)
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
						&& checkName(c,unresolvedTypeName,true))//c.rawName.equals(unresolvedTypeName))
				{
					return c;
				}
			}
		}

		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				//if (c.rawName.equals(unresolvedTypeName))
				if(checkName(c,unresolvedTypeName,true))
				{
					return c;
				}
			}
		}

		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CustomClassDefinition)
			{
				CustomClassDefinition c = (CustomClassDefinition) cd;

				//if (c.name.equals(unresolvedTypeName))
				if(checkName(c,unresolvedTypeName,false))
				{
					return c;
				}
			}
		}

		
		return null;// "%" + type;
	}
	
	private boolean checkName(IClassDefinition def, String name, boolean rawNameCheck)
	{
		if(name == null || name.trim().length()==0)
		{
			return true;
		}
		String nameToCheck = null;
		String rest = null;
		if(name.contains("."))
		{
			nameToCheck = name.substring(name.lastIndexOf('.')+1,name.length());
			rest = name.substring(0,name.lastIndexOf('.'));
		}else
		{
			nameToCheck = name;
		}
		
		if(rawNameCheck && def instanceof CommonTreeClassDefinition)
		{
			CommonTreeClassDefinition cDef = (CommonTreeClassDefinition) def;
			return cDef.rawName.equals(nameToCheck) && checkName(cDef.getSuperDef(), rest, rawNameCheck);
		}else if(def instanceof CustomClassDefinition)
		{
			CustomClassDefinition cDef = (CustomClassDefinition) def;
			return cDef.name.equals(nameToCheck) && checkName(cDef.getSuperDef(), rest, rawNameCheck);
		}
		return false;
	}

	public void setType(String text)
	{
		this.unresolvedType = text;
		this.type = getInternalType(unresolvedType);

	}

}
