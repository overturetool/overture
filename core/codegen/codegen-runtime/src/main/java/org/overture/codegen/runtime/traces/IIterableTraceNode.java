package org.overture.codegen.runtime.traces;

public interface IIterableTraceNode
{
	public abstract CallSequence get(int index);

	public abstract int size();
}