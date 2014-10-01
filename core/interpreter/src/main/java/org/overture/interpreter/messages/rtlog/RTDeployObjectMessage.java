package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.values.ObjectValue;

public class RTDeployObjectMessage extends RTArchitectureMessage
{

	public final ObjectValue object;
	public final CPUResource cpu;

	public RTDeployObjectMessage(ObjectValue object, CPUResource cpu)
	{
		this.object = object;
		this.cpu = cpu;
	}

	@Override
	String getInnerMessage()
	{
		return "DeployObj -> objref: " + object.objectReference + " clnm: \""
				+ object.type + "\"" + " cpunm: " + cpu.getNumber();
	}

	public CPUResource getCpu()
	{
		return this.cpu;
	}

	public Long getObjRef()
	{
		return Long.valueOf(this.object.objectReference);
	}

}
