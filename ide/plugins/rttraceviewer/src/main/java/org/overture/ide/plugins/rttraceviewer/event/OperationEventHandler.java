package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceOperation;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;

public class OperationEventHandler extends EventHandler {

	public OperationEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenOperationEvent oEvent = null;
		
		if(event instanceof NextGenOperationEvent)
			oEvent = (NextGenOperationEvent) event;
		else
			throw new IllegalArgumentException("OperationEventHandler expected event of type: " + NextGenOperationEvent.class.getName());
		
		//Exception will be thrown if it is not possible to look up the elements in data
		TraceCPU cpu = data.getCPU(new Long(oEvent.thread.cpu.id));
		TraceThread thread = data.getThread(new Long(oEvent.thread.id));
		String operationid = oEvent.operation.classDef.name + oEvent.operation.name;
		TraceOperation operation = data.getOperation(operationid);
		TraceObject destObj;
		
		//Check for Static object 
		if(oEvent.object == null) 
		{
			destObj = data.getStaticObject(oEvent.operation.classDef.name);
		}
		else
		{
			destObj = data.getObject(new Long(oEvent.object.id));
		}
	
		switch(oEvent.type)
		{
			
		case REQUEST: 
			eventViewer.drawOpRequest(tab, cpu, thread, destObj, operation);
			
			//Check for remote synchronous calls and update thread status to blocked
			if(!oEvent.operation.isAsync && oEvent.object != null && oEvent.object.cpu.id != oEvent.thread.cpu.id)
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
