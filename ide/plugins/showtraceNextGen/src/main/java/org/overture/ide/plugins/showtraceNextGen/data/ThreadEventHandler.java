package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.*;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThread.ThreadType;

public class ThreadEventHandler extends EventHandler {


	public ThreadEventHandler(TraceData data, ConjectureData conjectures) 
	{
		super(data, conjectures);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) 
	{
		NextGenThreadEvent tEvent = null;
		
		if(event instanceof NextGenThreadEvent)
			tEvent = (NextGenThreadEvent)event;
		else
			throw new IllegalArgumentException("ThreadEventhandler expected event of type: " + NextGenThreadEvent.class.getName());
		
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
				if(tEvent.thread.type == ThreadType.INIT)
					object = data.getInitThreadObject();
				else if(tEvent.thread.type == ThreadType.MAIN)
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
		
		return true;
	}
	

}
