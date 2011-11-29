package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.env.FieldTypeResolver;
import com.lausdahl.ast.creator.utils.NameUtil;

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
			imports.add(getInternalType(unresolvedType).getName().getCanonicalName());
			if (isTypeExternalNotNode())
			{
				imports.add(Environment.vectorDef.getName().getCanonicalName());
			}
			if (!isDoubleList)
			{
				switch (structureType)
				{
					case Graph:
						imports.add(env.graphNodeList.getName().getCanonicalName());
						break;
					case Tree:
						imports.add(env.nodeList.getName().getCanonicalName());
						break;

				}

			}
		}
		if (isDoubleList)
		{
			switch (structureType)
			{
				case Graph:
					imports.add(env.graphNodeListList.getName().getCanonicalName());
					break;
				case Tree:
					imports.add(env.nodeListList.getName().getCanonicalName());
					break;

			}
		}

		IInterfaceDefinition defIntf = env.lookUpInterface(getType());
		if (defIntf != null)
		{
			imports.add(defIntf.getName().getCanonicalName());
		}

		IClassDefinition def = env.lookUp(getType());
		if (def != null)
		{
			imports.add(def.getName().getCanonicalName());
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
		String tmp = (name == null ? type.getName().getName() : name);
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
		String internaalType = type.getName().getName();
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
					// internaalType = NameUtil.getGenericName(def.getName().getName(), type.getName().getName());//new
					// GenericArgumentedIInterfceDefinition(def, type).getName().getName();
				} else
				{
					if (structureType == StructureType.Tree)
					{
						def = env.nodeList;

					} else
					{
						def = env.graphNodeList;
					}
					// internaalType = NameUtil.getGenericName(def.getName().getName(), type.getName().getName());//new
					// GenericArgumentedIInterfceDefinition(def, type).getName().getName();
				}
				internaalType = NameUtil.getGenericName(def.getName().getName(), type.getName().getName());

			}
		}
		if (isDoubleList)
		{
			IInterfaceDefinition def = null;
			if (abstractType)
			{
				// def = new GenericArgumentedIInterfceDefinition(Environment.linkedListDef, new
				// GenericArgumentedIInterfceDefinition(Environment.listDef, type));
				// internaalType = def.getName().getName();
				internaalType = NameUtil.getGenericName(Environment.linkedListDef.getName().getName(), NameUtil.getGenericName(Environment.listDef.getName().getName(), type.getName().getName()));
			} else
			{
				if (structureType == StructureType.Tree)
				{
					def = env.nodeListList;

				} else
				{
					def = env.graphNodeListList;
				}
				internaalType = NameUtil.getGenericName(def.getName().getName(), type.getName().getName());// new
																											// GenericArgumentedIInterfceDefinition(def,
																											// type).getName().getName();
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
		String internaalType = type.getName().getName();
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
					className = def.getName().getName();
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

		String internaalType = type.getName().getName();
		return internaalType;
	}

	protected IInterfaceDefinition getInternalType(String unresolvedTypeName)
	{
		if (isTokenField)
		{
			return type;
		}

		//TODO return FieldTypeResolver.searchType(unresolvedTypeName, env);
		return FieldTypeResolver.searchTypePreferInterface(unresolvedTypeName, env);
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

	public String getCast()
	{
		return "(" + getType() + ")";
	}

	/**
	 * Only use this to merge unresolved fields
	 * 
	 * @return
	 */
	public String getUnresolvedType()
	{
		return this.unresolvedType;
	}

	public void updateEnvironment(Environment env)
	{
		this.env = env;
	}
}
