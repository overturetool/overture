package org.overture.codegen.runtime.traces;

abstract public class CallStatementPp implements CallStatement
{
	protected Object instance;
	
	public void setInstance(Object instance)
	{
		this.instance = instance;
	}
}