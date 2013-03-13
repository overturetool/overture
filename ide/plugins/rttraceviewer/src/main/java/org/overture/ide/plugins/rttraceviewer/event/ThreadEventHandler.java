package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.data.UnexpectedEventTypeException;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThread.ThreadType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;

public class ThreadEventHandler extends EventHandler {


	public ThreadEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) 
	{
		NextGenThreadEvent tEvent = null;
		
		if(event instanceof NextGenThreadEvent)
			tEvent = (NextGenThreadEvent)event;
		else
			throw new IllegalArgumentException("ThreadEventhandler expected event of type: " + NextGenThreadEvent.class.getName());
		
		if(tEvent.thread.type == ThreadType.INIT)
			return; //Ignore INIT threads
		
		Long cpuId = new Long(tEvent.thread.cpu.id);
		Long threadId = new Long(tEvent.thread.id);
		Long objectId = null;
		TraceCPU cpu = data.getCPU(cpuId);
		TraceThread thread = data.getThread(threadId);
		TraceObject object = null;
		
		switch(tEvent.type)
		{
		case CREATE: 			
			if(tEvent.thread.object == null)
			{
				if(tEvent.thread.type == ThreadType.MAIN)
					object = data.getMainThreadObject();
				else
					throw new UnexpectedEventTypeException("Did not expect create event in ThreadEventHandler for other thread types than init and main at this point.");
			}
			else
			{
				objectId = new Long(tEvent.thread.object.id);
				object = data.getObject(objectId);
			}
			thread.pushCurrentObject(object);
			eventViewer.drawThreadCreate(tab, cpu, thread);
			break;
		case SWAP: 
			throw new UnexpectedEventTypeException("Problem in ThreadEventHandler. SWAP events should be handled in " + ThreadSwapEventHandler.class.getName());
		case KILL: 
			eventViewer.drawThreadKill(tab, cpu, thread);
			if(thread.hasCurrentObject())
				thread.popCurrentObject();
			break;
		}
	}
	

}
