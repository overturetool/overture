package com.lausdahl.ast.values;

import com.lausdahl.runtime.Context;


public interface IValue
{
	public boolean boolValue(Context ctxt);
	public int intValue(Context ctxt);
	public double realValue(Context ctxt);
}
