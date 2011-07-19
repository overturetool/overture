package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;

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

	public static enum StructureType
	{
		Tree, Graph
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
	public boolean isDoubleList = false;
	public StructureType structureType = StructureType.Tree;

	public Field(Environment env)
	{
		this.env = env;
	}

	public List<String> getRequiredImports()
	{
		List<String> imports = new Vector<String>();
		if (isList)
		{
			imports.add(getInternalType(unresolvedType).getImportName());
			if (isTypeExternalNotNode())
			{
				imports.add(Environment.vectorDef.getImportName());
			}
			if(!isDoubleList)
			{
				switch (structureType)
				{
					case Graph:
						imports.add(env.graphNodeList.getImportName());
						break;
					case Tree:
						imports.add(env.nodeList.getImportName());
						break;
					
				}
				
			}
		}
		if (isDoubleList)
		{
			switch (structureType)
			{
				case Graph:
					imports.add(env.graphNodeListList.getImportName());
					break;
				case Tree:
					imports.add(env.nodeListList.getImportName());
					break;
				
			}
		}

		IInterfaceDefinition defIntf = env.lookUpInterface(getType());
		if (defIntf != null)
		{
			imports.add(defIntf.getImportName());
		}

		IClassDefinition def = env.lookUp(getType());
		if (def != null)
		{
			imports.add(def.getImportName());
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

	public String getType(boolean abstractType)
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		checkType(type);
		String internaalType = type.getName();
		if (isList && !isDoubleList)
		{
			if (isTypeExternalNotNode())
			{
				internaalType = "List<? extends " + internaalType + ">";
			} else
			{
				IInterfaceDefinition def = null;
				if (abstractType)
				{
					def = Environment.linkedListDef;
					internaalType = new GenericArgumentedIInterfceDefinition(def, type).getName();
				} else
				{
					if (structureType == StructureType.Tree)
					{
						def = env.nodeList;

					} else
					{
						def = env.graphNodeList;
					}
					internaalType = new GenericArgumentedIInterfceDefinition(def, type).getName();
				}

			}
		}
		if (isDoubleList)
		{
			IInterfaceDefinition def = null;
			if (abstractType)
			{
				def = new GenericArgumentedIInterfceDefinition(Environment.linkedListDef, new GenericArgumentedIInterfceDefinition(Environment.listDef, type));
				internaalType = def.getName();
			} else
			{
				if (structureType == StructureType.Tree)
				{
					def = env.nodeListList;

				} else
				{
					def = env.graphNodeListList;
				}
				internaalType = new GenericArgumentedIInterfceDefinition(def, type).getName();
			}

		}

		return internaalType;
	}

	public String getType()
	{
		return getType(false);
	}

	public String getMethodArgumentType()
	{
		// String internaalType = getInternalType();
		if (type == null)
		{
			type = getInternalType(unresolvedType);
		}
		checkType(type);
		String internaalType = type.getName();
		if (isList && !isDoubleList)
		{

			return "List<? extends " + internaalType + ">";
		}
		if (isDoubleList)
		{
			return "Collection<? extends List<" + internaalType + ">>";
		}
		return internaalType;
	}

	public void checkType(IInterfaceDefinition t)
	{
		if (t == null)
		{
			String msg = ("Unable to resolve type for field: \"" + getName()
					+ " : " + unresolvedType + "\" in class %s with raw type " + unresolvedType);
			String className = "";
			for (IClassDefinition def : env.getClasses())
			{
				if (def.getFields() != null && def.getFields().contains(this))
				{
					className = def.getName();
				}

			}
			System.out.println();
			System.err.println(String.format(msg, className));
			throw new Error(msg);
		}
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

		// First look up all tokens
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				if (c.getType() == CommonTreeClassDefinition.ClassType.Token
						&& checkName(c, unresolvedTypeName, true))// c.rawName.equals(unresolvedTypeName))
				{
					return c;
				}
			}
		}

		// Lookup in all root productions
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				// if (c.rawName.equals(unresolvedTypeName))
				if (c.getType() == ClassType.Production
						&& checkName(c, unresolvedTypeName, true))
				{
					return c;
				}
			}
		}
		// Lookup in all sub productions
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				// if (c.rawName.equals(unresolvedTypeName))
				if (c.getType() == ClassType.SubProduction
						&& checkName(c, unresolvedTypeName, true))
				{
					return c;
				}
			}
		}

		// Lookup in all alternatives
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				// if (c.rawName.equals(unresolvedTypeName))
				if (c.getType() == ClassType.Alternative
						&& checkName(c, unresolvedTypeName, true))
				{
					return c;
				}
			}
		}

		// Lookup for all raw names no matter the type
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CommonTreeClassDefinition)
			{
				CommonTreeClassDefinition c = (CommonTreeClassDefinition) cd;

				// if (c.rawName.equals(unresolvedTypeName))
				if (checkName(c, unresolvedTypeName, true))
				{
					return c;
				}
			}
		}

		// Lookup in all with not raw name
		for (IClassDefinition cd : env.getClasses())
		{
			if (cd instanceof CustomClassDefinition)
			{
				CustomClassDefinition c = (CustomClassDefinition) cd;

				// if (c.name.equals(unresolvedTypeName))
				if (checkName(c, unresolvedTypeName, false))
				{
					return c;
				}
			}
		}

		return null;// "%" + type;
	}

	private boolean checkName(IClassDefinition def, String name,
			boolean rawNameCheck)
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

		if (rawNameCheck && def instanceof CommonTreeClassDefinition)
		{
			CommonTreeClassDefinition cDef = (CommonTreeClassDefinition) def;
			return cDef.rawName.equals(nameToCheck)
					&& checkName(cDef.getSuperDef(), rest, rawNameCheck);
		} else if (def instanceof CustomClassDefinition)
		{
			CustomClassDefinition cDef = (CustomClassDefinition) def;
			return cDef.name.equals(nameToCheck)
					&& checkName(cDef.getSuperDef(), rest, rawNameCheck);
		}
		return false;
	}

	public void setType(String text)
	{
		this.unresolvedType = text;
		// this.type = getInternalType(unresolvedType);

	}

	public boolean isTypeExternalNotNode()
	{
		return (type instanceof ExternalJavaClassDefinition && !((ExternalJavaClassDefinition) type).extendsNode);
	}
}
