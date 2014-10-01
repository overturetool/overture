package org.overture.interpreter.traces;

public interface IIterableTraceNode
{

	public abstract CallSequence get(int index);

	public abstract int size();

}