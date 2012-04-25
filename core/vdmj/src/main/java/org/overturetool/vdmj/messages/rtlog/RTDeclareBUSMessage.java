package org.overturetool.vdmj.messages.rtlog;

public class RTDeclareBUSMessage extends RTArchitectureMessage
{

	private int busNumber;
	private String cpus;
	private String name;

	public RTDeclareBUSMessage(int busNumber, String cpusToSet, String name)
	{
		this.busNumber = busNumber;
		this.cpus =cpusToSet;
		this.name = name;
	}

	@Override
	String getInnerMessage()
	{
		return "BUSdecl -> id: " + busNumber +
		" topo: " + cpus +
		" name: \"" + name + "\"";
	}

}
