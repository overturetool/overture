package com.lausdahl.ast.creator.utils;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.java.definitions.JavaName;

public class EnumUtil
{
	public static String getEnumElementName(IClassDefinition c)
	{
		return NameUtil.getClassName(c.getName().getRawName().startsWith("#") ? c.getName().getRawName().substring(1)
				: c.getName().getRawName()).toUpperCase();
	}

	public static String getEnumTypeName(IClassDefinition c, Environment env)
	{
		return getEnumTypeNameNoPostfix(c, env);// + c.getName().getPostfix();

	}

	public static String getEnumTypeNameNoPostfix(IClassDefinition c,
			Environment env)
	{
		JavaName name = env.getInterfaceForCommonTreeNode(c).getName();
		
		if(c==env.node)
		{
			return "NodeEnum"+env.node.getName().getPostfix();
		}
		if (env.classToType.get(c) == ClassType.Production)
		{

			return "E"
					+ NameUtil.getClassName(name.getRawName());
		} else
		{
//			String tmp = c.getName().getRawName().startsWith("#") ? c.getName().getRawName().substring(1)
//					: c.getName().getRawName();
//			String superTmp = c.getSuperDef().getName().getRawName();
//			superTmp = superTmp.startsWith("#") ? superTmp.substring(1)
//					: superTmp;
//			return "E" + NameUtil.getClassName(tmp)
//					+ NameUtil.getClassName(superTmp);
//			String tmp = c.getName().getRawName().startsWith("#") ? c.getName().getRawName().substring(1)
//					: c.getName().getRawName();
//			String superTmp = c.getSuperDef().getName().getRawName();
//			superTmp = superTmp.startsWith("#") ? superTmp.substring(1)
//					: superTmp;
			return "E" + NameUtil.getClassName(name.getRawName())
					+ NameUtil.getClassName(env.getInterfaceForCommonTreeNode(c.getSuperDef()).getName().getRawName());
		}
	}
}
