package org.overture.tools.astcreator.methods.visitors;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.env.Environment;

public class AnalysisUtil
{

	public static IInterfaceDefinition getClass(Environment env,
			IClassDefinition c)
	{
		if (env.isTreeNode(c))
		{
			switch (env.classToType.get(c))
			{
				case Production:
				case SubProduction:
					return env.getInterfaceForCommonTreeNode(c);
				case Token:
//					if (c instanceof ExternalJavaClassDefinition)
//					{
					return env.iToken;
//					} else
//					{
//						return env.getInterfaceForCommonTreeNode(c.getSuperDef());
//					}
				default:
					break;
			}
		}
		return c;
	}
	
	
	public static IInterfaceDefinition getCaseClass(Environment env,
			IClassDefinition c)
	{
		if (env.isTreeNode(c))
		{
			switch (env.classToType.get(c))
			{
				case Production:
				case SubProduction:
					return env.getInterfaceForCommonTreeNode(c);
				default:
					break;				
			}
		}
		return c;
	}

}
