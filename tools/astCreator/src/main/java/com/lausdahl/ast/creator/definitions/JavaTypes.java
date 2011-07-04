package com.lausdahl.ast.creator.definitions;

import java.util.List;
import java.util.Vector;

public class JavaTypes
{
	static final List<String> primitiveTypes = new Vector<String>();
	static
	{
		primitiveTypes.add("byte");
		primitiveTypes.add("short");
		primitiveTypes.add("int");
		primitiveTypes.add("long");
		primitiveTypes.add("float");
		primitiveTypes.add("double");
		primitiveTypes.add("char");
//		primitiveTypes.add("String");
		primitiveTypes.add("boolean");

		
		primitiveTypes.add("Byte");
		primitiveTypes.add("Short");
		primitiveTypes.add("Integer");
		primitiveTypes.add("Long"); 
		primitiveTypes.add("Float");
		primitiveTypes.add("Double");
		primitiveTypes.add("Character");
		primitiveTypes.add("String");
		primitiveTypes.add("Boolean");

	}
	
	public static boolean isPrimitiveType(String typeName)
	{
		return primitiveTypes.contains(typeName);
	}
}
