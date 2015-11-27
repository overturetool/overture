package org.overture.codegen.runtime.traces;

abstract public class CallStatementPp implements CallStatementSl
{
	protected Object instance;
	
	public void setInstance(Object instance)
	{
		this.instance = instance;
	}
}