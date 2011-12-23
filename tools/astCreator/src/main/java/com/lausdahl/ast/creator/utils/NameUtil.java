package com.lausdahl.ast.creator.utils;

import java.util.Arrays;
import java.util.Iterator;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IInterfaceDefinition;
import com.lausdahl.ast.creator.env.Environment;

public class NameUtil
{
	public static String getAssembledNamePostFix(Environment env, IClassDefinition c)
	{
		IClassDefinition tmpSuper = c;
		String name = "";
		boolean stop = false;
		while(tmpSuper!=null && env.isTreeNode(tmpSuper)&& !stop )
		{
			switch(env.classToType.get(tmpSuper))
			{
				case Production:
					stop = true;
				case SubProduction:
					name+=tmpSuper.getName().getRawName();
					break;
			}
			tmpSuper = tmpSuper.getSuperDef();
		}
		return name;
	}
	
	
	public static String getGenericName(IInterfaceDefinition def)
	{
		String name = def.getName().getName();
		if (!def.getGenericArguments().isEmpty())
		{
			name += "<";
			for (Iterator<String> itr = def.getGenericArguments().iterator(); itr.hasNext();)
			{
				String type = itr.next();
				name += type;
				if (itr.hasNext())
				{
					name += ", ";
				}
			}
			name += ">";
		}

		return name;
	}
	
	public static String getGenericName(String name,String arguments)
	{
		if (!arguments.isEmpty())
		{
			name += "<";
			for (Iterator<String> itr = Arrays.asList(arguments).iterator(); itr.hasNext();)
			{
				String type = itr.next();
				name += type;
				if (itr.hasNext())
				{
					name += ", ";
				}
			}
			name += ">";
		}

		return name;
	}
	
	public static String getClassName(String name)
	{
		return javaClassName(firstLetterUpper(name));
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


public static String stripGenerics(String name)
{

if(name.contains("<"))
{
return name.substring(0,name.indexOf('<'));
}
return name;
}
}
