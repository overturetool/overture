package com.lausdahl.ast.values;

import com.lausdahl.runtime.Context;

public class IntegerValue extends Value
{

	public final int value;

	public IntegerValue(int value)
	{
		this.value = value;
	}

	@Override
	public int intValue(Context ctxt)
	{
		return value;
	}

	@Override
	public double realValue(Context ctxt)
	{
		return value;
	}

	@Override
	public String toString()
	{
		return new Integer(value).toString();
	}

}
