package org.overture.interpreter.messages.rtlog;

public class RTDeclareBUSMessage extends RTArchitectureMessage
{

	public int busNumber;
	public String cpus;
	public String name;

	public RTDeclareBUSMessage(int busNumber, String cpusToSet, String name)
	{
		this.busNumber = busNumber;
		this.cpus = cpusToSet;
		this.name = name;
	}

	@Override
	String getInnerMessage()
	{
		return "BUSdecl -> id: " + busNumber + " topo: " + cpus + " name: \""
				+ name + "\"";
	}

}
