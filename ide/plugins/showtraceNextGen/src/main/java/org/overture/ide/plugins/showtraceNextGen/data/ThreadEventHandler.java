package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.*;

public class ThreadEventHandler extends EventHandler {


	public ThreadEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) 
	{
		NextGenThreadEvent tEvent = (NextGenThreadEvent)event;
		if(tEvent == null) return false; //Guard
		
		Long cpuId = new Long(tEvent.thread.cpu.id);
		Long threadId = new Long(tEvent.thread.id);
		TraceCPU cpu = data.getCPU(cpuId);
		TraceThread thread = data.getThread(threadId);
				
		switch(tEvent.type)
		{
		case CREATE: 
			eventViewer.drawThreadCreate(tab, cpu);
			cpu.addThreadId(threadId);
			break;
		case SWAP: 
			return false;
		case KILL: 
			eventViewer.drawThreadKill(tab, cpu);
			if(thread.hasCurrentObject())
				thread.popCurrentObject();
			break;
		}
		
		return true;
	}
	

}
