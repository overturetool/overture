package org.overture.interpreter.messages.rtlog;

public class RTDeclareCPUMessage extends RTArchitectureMessage
{

	public int cpuId;
	public boolean expl;
	public String sysName;
	public String cpuName;

	public RTDeclareCPUMessage(int cpuId, boolean expl, String sysName,
			String cpuName)
	{
		this.cpuId = cpuId;
		this.expl = expl;
		this.sysName = sysName;
		this.cpuName = cpuName;
	}

	public RTDeclareCPUMessage(int cpuId, String cpuName)
	{
		this(cpuId, false, "", cpuName);
	}

	@Override
	String getInnerMessage()
	{
		return "CPUdecl -> id: " + cpuId + " expl: " + expl + " sys: \""
				+ sysName + "\"" + " name: \"" + cpuName + "\"";
	}

}
