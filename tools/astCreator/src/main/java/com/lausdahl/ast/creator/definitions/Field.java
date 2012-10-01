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
	private String unresolvedType;
	public AccessSpecifier accessspecifier = AccessSpecifier.Private;
	public boolean isDoubleList = false;
	public StructureType structureType = StructureType.Tree;
	private String customInitializer ="";

	public Field()
	{
	}

	public List<String> getRequiredImports(Environment env)
	{
		List<String> imports = new Vector<String>();
		if (isList)
		{
			imports.add(getInternalType(unresolvedType, env).getName().getCanonicalName());
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

		IInterfaceDefinition defIntf = env.lookUpInterface(getType(env));
		if (defIntf != null)
		{
			imports.add(defIntf.getName().getCanonicalName());
		}

		IClassDefinition def = env.lookUp(getType(env));
		if (def != null)
		{
			imports.add(def.getName().getCanonicalName());
		}

		return imports;
	}

	@Override
	public String toString()
	{
//		String name = null;
//		try
//		{
//			name = getName();
//		} catch (Exception e)
//		{
//
//		}
		return this.name;
	}

	public String getName(Environment env)
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType, env);
		}
		String tmp = (name == null ? type.getName().getName() : name);
		return fieldPrefic + tmp.substring(0, 1).toLowerCase()
				+ tmp.substring(1);
	}

	public String getType(boolean abstractType, Environment env)
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType,env);
		}
		checkType(type,env);
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
//		if(type instanceof GenericArgumentedIInterfceDefinition)
//		{
//			return type.getName()+type.getGenericsString();
//		}

		return internaalType;
	}

	public String getType(Environment env)
	{
		return getType(false, env);
	}

	public String getMethodArgumentType(Environment env)
	{
		// String internaalType = getInternalType();
		if (type == null)
		{
			type = getInternalType(unresolvedType,env);
		}
		checkType(type,env);
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

	public void checkType(IInterfaceDefinition t, Environment env)
	{
		if (t == null)
		{
			String msg = ("Unable to resolve type for field: \"" + name
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

	public String getInnerTypeForList(Environment env)
	{
		if (type == null)
		{
			type = getInternalType(unresolvedType, env);
		}

		String internaalType = type.getName().getName();
		return internaalType;
	}

	protected IInterfaceDefinition getInternalType(String unresolvedTypeName, Environment env)
	{
		if (isTokenField)
		{
			return type;
		}

		//TODO return FieldTypeResolver.searchType(unresolvedTypeName, env);
		return FieldTypeResolver.searchTypePreferInterface(unresolvedTypeName, env,this);
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

	public String getCast(Environment env)
	{
		return "(" + getType(env) + ")";
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


	public boolean hasCustomInitializer()
	{
		return !this.customInitializer.isEmpty();
	}

	public String getCustomInitializer()
	{
		return this.customInitializer;
	}
	
	public void setCustomInitializer(String customInitializer)
	{
		this.customInitializer = customInitializer;
	}
}
