package com.lausdahl.ast.values;

import com.lausdahl.runtime.Context;







public class BooleanValue extends Value
{

	public final boolean value;

	public BooleanValue(boolean value)
	{
		this.value = value;
	}

	@Override
	public boolean boolValue(Context ctxt)
	{
		return value;
	}

	@Override
	public String toString()
	{
		return new Boolean(value).toString();
	}
}
