package com.lausdahl.ast.values;

import com.lausdahl.runtime.Context;







public class RealValue extends Value
{

	public final double value;

	public RealValue(double value)
	{
		this.value = value;
	}

	@Override
	public int intValue(Context ctxt)
	{
		return (int) value;
	}

	@Override
	public double realValue(Context ctxt)
	{
		return value;
	}

	@Override
	public String toString()
	{
		return new Double(value).toString();
	}
}
