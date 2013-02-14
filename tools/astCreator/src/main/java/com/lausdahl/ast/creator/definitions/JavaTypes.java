package com.lausdahl.ast.creator.definitions;

public class JavaTypes
{
	// static final List<String> primitiveTypes = new Vector<String>();
	// static
	// {
	// primitiveTypes.add("byte");
	// primitiveTypes.add("short");
	// primitiveTypes.add("int");
	// primitiveTypes.add("long");
	// primitiveTypes.add("float");
	// primitiveTypes.add("double");
	// primitiveTypes.add("char");
	// // primitiveTypes.add("String");
	// primitiveTypes.add("boolean");
	//
	//
	// primitiveTypes.add("Byte");
	// primitiveTypes.add("Short");
	// primitiveTypes.add("Integer");
	// primitiveTypes.add("Long");
	// primitiveTypes.add("Float");
	// primitiveTypes.add("Double");
	// primitiveTypes.add("Character");
	// primitiveTypes.add("String");
	// primitiveTypes.add("Boolean");
	//
	// }

	enum PrimitiveJavaTypeEnum
	{
		_byte("byte"), _short("short"), _int("int"), _long("long"), _float(
				"float"), _double("double"), _char("char"),
		// primitiveTypes.add("String");
		_boolean("boolean"),

		_Byte("Byte"), _Short("Short"), _Integer("Integer"), _Long("Long"), _Float(
				"Float"), _Double("Double"), _Character("Character"), _String(
				"String"), _Boolean("Boolean");

		public final String name;

		private PrimitiveJavaTypeEnum(String name)
		{
			this.name = name;
		}

		public static boolean contains(String name)
		{
			for (PrimitiveJavaTypeEnum v : values())
			{
				if (v.name.equals(name))
				{
					return true;
				}
			}
			return false;
		}

		public static PrimitiveJavaTypeEnum getEnumFromName(String name)
		{
			for (PrimitiveJavaTypeEnum v : values())
			{
				if (v.name.equals(name))
				{
					return v;
				}
			}
			return null;
		}
	}

	public static boolean isPrimitiveType(String typeName)
	{
		// return primitiveTypes.contains(typeName);
		return PrimitiveJavaTypeEnum.contains(typeName);
	}

	public static String getDefaultValue(String typeName)
	{
		PrimitiveJavaTypeEnum type = PrimitiveJavaTypeEnum.getEnumFromName(typeName);
		if (type != null)
		{
			switch (type)
			{
				case _Boolean:
				case _boolean:
					return "false";
				case _Byte:
				case _byte:
				case _Short:
				case _short:
				case _Integer:
				case _int:
					return "0";
				case _Character:
				case _char:
					return "'\u0000'";
				case _Double:
				case _double:
					return "0.0d";
				case _Float:
				case _float:
					return "0.0f";
				case _Long:
				case _long:
					return "0L";
				case _String:
					return "null";

			}
		}
		return "null";
	}
}
