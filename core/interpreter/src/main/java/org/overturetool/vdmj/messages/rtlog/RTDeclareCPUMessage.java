package org.overturetool.vdmj.messages.rtlog;


public class RTDeclareCPUMessage extends RTArchitectureMessage
{

	private int cpuId;
	private boolean expl;
	private String sysName;
	private String cpuName;
	
	public RTDeclareCPUMessage(int cpuId, boolean expl, String sysName, String cpuName)
	{
		this.cpuId = cpuId;
		this.expl = expl;
		this.sysName = sysName;
		this.cpuName = cpuName;
	}

	@Override
	String getInnerMessage()
	{
		return "CPUdecl -> id: " + cpuId +
		" expl: " + expl +
		" sys: \"" + sysName + "\"" +
		" name: \"" + cpuName + "\"";
	}

}
