package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.values.CPUValue;

public class RTDeployStaticMessage extends RTArchitectureMessage
{
	private String clnm;
	private int objRef;
	
	static int staticReferenceId=Integer.MAX_VALUE;
	
	
	public RTDeployStaticMessage(String name)
	{
		this.clnm = name;
		//need unique id
		objRef = --staticReferenceId;
	}

	@Override
	String getInnerMessage()
	{
		return "DeployObj -> objref: " + objRef +
		" clnm: \"" + clnm + "\"" +
		" cpunm: " + CPUValue.vCPU.getNumber();
	}
	
	public Long getObjectReference()
	{
		return Long.valueOf(objRef);
	}

}
