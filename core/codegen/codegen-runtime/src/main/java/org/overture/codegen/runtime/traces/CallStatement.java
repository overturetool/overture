package org.overture.codegen.runtime.traces;

abstract public class CallStatement implements Statement
{
	abstract public Object execute();

	public Boolean isTypeCorrect()
	{
		return true;
	}

	public Boolean meetsPreCond()
	{
		return true;
	}
}
