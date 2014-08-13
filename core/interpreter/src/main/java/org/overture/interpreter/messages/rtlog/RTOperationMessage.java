package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.values.OperationValue;

public class RTOperationMessage extends RTMessage
{
	public static boolean inSystemConstruction = true; // set to false in SystemDefinition when systemInit finishes

	public MessageType messageType;
	public OperationValue operationVal;
	public CPUResource from;
	public long threadId;
	public final boolean madeDuringSystemConstruction;
	public Long objref;

	public RTOperationMessage(MessageType messageType,
			OperationValue operationVal, CPUResource from, long threadId)
	{
		this.messageType = messageType;
		this.operationVal = operationVal;
		this.from = from;
		this.threadId = threadId;
		this.madeDuringSystemConstruction = inSystemConstruction;
	}

	@Override
	String getInnerMessage()
	{
		if (!operationVal.isStatic)
		{
			return "Op" + messageType + " -> id: " + threadId + " opname: \""
					+ operationVal.name + "\"" + " objref: "
					+ operationVal.getSelf().objectReference + " clnm: \""
					+ operationVal.getSelf().type.getName().getName() + "\""
					+ " cpunm: " + from.getNumber() + " async: "
					+ operationVal.isAsync;
		} else
		{
			return "Op" + messageType + " -> id: " + threadId
					+ " opname: \""
					+ operationVal.name
					+ "\""
					+
					// " objref: nil" +
					" objref: " + objref + " clnm: \""
					+ operationVal.classdef.getName().getName() + "\""
					+ " cpunm: " + from.getNumber() + " async: "
					+ operationVal.isAsync;
		}
	}

	@Override
	public void generateStaticDeploys()
	{
		try
		{
			if (operationVal.isStatic)
			{
				objref = getStaticId(operationVal.classdef.getName().getName(), from);
			} else
			{
				objref = Long.valueOf(operationVal.getSelf().objectReference);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateCpu(CPUResource newCpu)
	{
		this.from = newCpu;
	}

	public CPUResource getCpu()
	{
		return this.from;
	}

	public Long getObjRef()
	{
		return this.objref;
	}

}
