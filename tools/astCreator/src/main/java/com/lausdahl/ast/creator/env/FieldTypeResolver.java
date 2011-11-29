package com.lausdahl.ast.creator.env;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;

public class FieldTypeResolver
{
	public static IInterfaceDefinition searchTypePreferInterface(String unresolvedTypeName,
			Environment env)
	{
		IInterfaceDefinition type = searchType(unresolvedTypeName, env);
		IInterfaceDefinition intf = env.getInterfaceForCommonTreeNode(type);
		if(intf==null)
		{
			return type;
		}
		return intf;
	}
	public static IInterfaceDefinition searchType(String unresolvedTypeName,
			Environment env)
	{
		// First look up all tokens
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.isTreeNode(cd))
			{
				if (env.classToType.get(cd) == ClassType.Token
						&& checkName(cd, unresolvedTypeName, true, env))// c.rawName.equals(unresolvedTypeName))
				{
					return cd;
				}
			}
		}

		// Lookup in all root productions
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.isTreeNode(cd))
			{
				if (env.classToType.get(cd) == ClassType.Production
						&& checkName(cd, unresolvedTypeName, true, env))
				{
					return cd;
				}
			}
		}
		// Lookup in all sub productions
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.isTreeNode(cd))
			{
				if (env.classToType.get(cd) == ClassType.SubProduction
						&& checkName(cd, unresolvedTypeName, true, env))
				{
					return cd;
				}
			}
		}

		// Lookup in all alternatives
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.isTreeNode(cd))
			{
				if (env.classToType.get(cd) == ClassType.Alternative
						&& checkName(cd, unresolvedTypeName, true, env))
				{
					return cd;
				}
			}
		}

		// Lookup for all raw names no matter the type
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.isTreeNode(cd))
			{
				if (checkName(cd, unresolvedTypeName, true, env))
				{
					return cd;
				}
			}
		}

		// Lookup in all with not raw name
		for (IClassDefinition cd : env.getClasses())
		{
			if (env.classToType.get(cd) == ClassType.Custom)
			{
				if (checkName(cd, unresolvedTypeName, false, env))
				{
					return cd;
				}
			}
		}

		for (IClassDefinition c : env.getClasses())
		{
			if (c.getName().equals(unresolvedTypeName))
			{
				return c;
			}
		}

		for (IClassDefinition c : env.getClasses())
		{
			if (c.getName().getTag().equals(unresolvedTypeName))
			{
				return c;
			}
		}

		return null;// "%" + type;

	}

	private static boolean checkName(IClassDefinition def, String name,
			boolean rawNameCheck, Environment env)
	{
		if (name == null || name.trim().length() == 0)
		{
			return true;
		}
		String nameToCheck = null;
		String rest = null;
		if (name.contains("."))
		{
			nameToCheck = name.substring(name.lastIndexOf('.') + 1, name.length());
			rest = name.substring(0, name.lastIndexOf('.'));
		} else
		{
			nameToCheck = name;
		}

		if (rawNameCheck && env.isTreeNode(def))
		{
			return def.getName().getTag().equals(nameToCheck)
					&& checkName(def.getSuperDef(), rest, rawNameCheck, env);
		} else if (env.classToType.get(def) == ClassType.Custom)
		{
			return def.getName().getName().equals(nameToCheck)
					&& checkName(def.getSuperDef(), rest, rawNameCheck, env);
		}
		return false;
	}
}
