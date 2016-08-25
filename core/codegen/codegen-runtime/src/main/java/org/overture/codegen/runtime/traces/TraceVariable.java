package org.overture.codegen.runtime.traces;

public class TraceVariable implements Statement
{
	private String name;
	private String type;
	private String value;

	public TraceVariable(String name, String type, String value)
	{
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return name + ":" + type + " = " + value;
	}
}
