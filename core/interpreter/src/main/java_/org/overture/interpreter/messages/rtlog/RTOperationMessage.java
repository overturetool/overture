package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.values.OperationValue;


public class RTOperationMessage extends RTMessage
{
	public static boolean inSystemConstruction = true; //set to false in SystemDefinition when systemInit finishes
	
	private MessageType messageType;
	private OperationValue operationVal;
	private CPUResource from;
	private long threadId;
	public final boolean madeDuringSystemConstruction;
	private Long objref;
	
	public RTOperationMessage(MessageType messageType, OperationValue operationVal, CPUResource from, long threadId)
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
			return "Op"+messageType+" -> id: " + threadId +
			" opname: \"" + operationVal.name + "\"" +
			" objref: " + operationVal.getSelf().objectReference +
			" clnm: \"" + operationVal.getSelf().type.name.name + "\"" +
			" cpunm: " +  from.getNumber() +
			" async: " + operationVal.isAsync;
		}else
		{
			return "Op"+messageType+" -> id: " + threadId +
			" opname: \"" + operationVal.name + "\"" +
			//" objref: nil" +
			" objref: "+ objref +
			" clnm: \"" + operationVal.classdef.name.name + "\"" +
			" cpunm: " +  from.getNumber() +
			" async: " + operationVal.isAsync;
		}
	}
	
	@Override
	public void generateStaticDeploys()
	{try{
		if (operationVal.isStatic){
			objref = getStaticId(operationVal.classdef.name.name,from);
		}else
		{
			objref = Long.valueOf(operationVal.getSelf().objectReference);
		}
	}catch(Exception e)
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
