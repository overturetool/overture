package org.overture.interpreter.traces.util;

public class Pair<T1, T2>
{
	public Pair(T1 first, T2 second)
	{
		this.first = first;
		this.second = second;
	}

	public T1 first;
	public T2 second;

	@Override
	public String toString()
	{
		return "(" + first + ":" + second + ")";
	}
}