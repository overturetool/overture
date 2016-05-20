package org.overture.codegen.runtime;

public class VDMUtil
{
	private VDMUtil() {
	}

	public static String val2seq_of_char(Object value)
	{
		return Utils.toString(value);
	}

	public static String classname(Object obj)
	{
		if(obj != null && obj.getClass().getEnclosingClass() == null)
		{
			return obj.getClass().getSimpleName();
		}
		else
		{
			return null;
		}
	}
}
