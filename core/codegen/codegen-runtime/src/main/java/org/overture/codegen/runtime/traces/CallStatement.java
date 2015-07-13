package org.overture.codegen.runtime.traces;

abstract public class CallStatement
{
	abstract public Object execute(final Object instance);
	
	@Override
	abstract public String toString();
}
