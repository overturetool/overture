package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.*;

public class OperationEventHandler extends EventHandler {

	public OperationEventHandler(TraceData data, ConjectureData conjectures) 
	{
		super(data, conjectures);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenOperationEvent oEvent = null;
		
		if(event instanceof NextGenOperationEvent)
			oEvent = (NextGenOperationEvent) event;
		else
			throw new IllegalArgumentException("OperationEventHandler expected event of type: " + NextGenOperationEvent.class.getName());
		
		 //XXX: Ignore util operations
		if(oEvent.object == null)
			return;
		
		//Exception will be thrown if it is not possible to look up the elements in data
		Long cpuId = new Long(oEvent.thread.cpu.id);
		TraceCPU cpu = data.getCPU(cpuId);
		
		Long threadId = new Long(oEvent.thread.id);
		TraceThread thread = data.getThread(threadId);

		Long destObjId = new Long(oEvent.object.id);
		TraceObject destObj = data.getObject(destObjId);
		
		String operationid = oEvent.operation.classDef.name + oEvent.operation.name;
		TraceOperation operation = data.getOperation(operationid);
		
		switch(oEvent.type)
		{
			
		case REQUEST: 
			eventViewer.drawOpRequest(tab, cpu, thread, destObj, operation);
			
			//Check for remote synchronous calls and update thread status to blocked
			if(!oEvent.operation.isAsync && oEvent.object.cpu.id != oEvent.thread.cpu.id)
			{
            	thread.setStatus(true);         		
			}		
			break;
		case ACTIVATE: 
			eventViewer.drawOpActivate(tab,  cpu, thread, destObj, operation);
			thread.pushCurrentObject(destObj);
			break;
		case COMPLETE: 
			thread.popCurrentObject();
			eventViewer.drawOpCompleted(tab,  cpu, thread, destObj, operation);
			break;
		default: 
			throw new IllegalArgumentException("Invalid Operation Event");
		}
		
		return;
	}



}
