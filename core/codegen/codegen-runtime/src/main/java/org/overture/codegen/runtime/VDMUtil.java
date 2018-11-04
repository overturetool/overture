package org.overture.codegen.runtime;

public class VDMUtil
{
	private static final String NOT_SUPPORTED_MSG = "Function is currently not supported";

	@SuppressWarnings("unchecked")
	public static VDMSeq set2seq(Object obj)
	{
		if (!(obj instanceof VDMSet))
		{
			throw new IllegalArgumentException("Expected a set but got: " + Utils.toString(obj));
		}
		
		VDMSet set = (VDMSet) obj;
		
		VDMSeq seq = SeqUtil.seq();
		seq.addAll(set);
		return seq;
	}

	public static Object get_file_pos()
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	public static String val2seq_of_char(Object value)
	{
		return Utils.toString(value);
	}

	public static Tuple seq_of_char2val(Object s)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	private static Tuple seq_of_char2val_(Object s)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	public static String classname(Object obj)
	{
		if (obj != null && obj.getClass().getEnclosingClass() == null)
		{
			return obj.getClass().getSimpleName();
		} else
		{
			return null;
		}
	}
}
