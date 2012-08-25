package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.*;

public class ThreadSwapEventHandler extends EventHandler {

	public ThreadSwapEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) 
	{
		NextGenThreadSwapEvent tEvent = (NextGenThreadSwapEvent)event;
		if(tEvent == null) return false;
		
		Long cpuId = new Long(tEvent.thread.cpu.id);
		Long threadId = new Long(tEvent.thread.id);
		TraceCPU cpu = data.getCPU(cpuId);
		TraceThread thread = data.getThread(threadId);
		TraceObject object = null;
		if(tEvent.thread.object != null)
		{
			Long objectId = new Long(tEvent.thread.object.id);
			object = data.getObject(objectId);
		}
		
		switch(tEvent.swapType)
		{
			case SWAP_IN: 
				eventViewer.drawThreadSwapIn(tab, cpu, thread); 
				cpu.setCurrentThread(threadId);
				cpu.setIdle(false);
				break;
			case DELAYED_IN: 
				eventViewer.drawDelayedThreadSwapIn(tab, cpu, thread); 
				cpu.setCurrentThread(threadId);
				cpu.setIdle(false);
				break;
			case SWAP_OUT: 
				eventViewer.drawThreadSwapOut(tab, cpu, thread); 
				cpu.setCurrentThread(null);
				cpu.setIdle(true);
				break;
		}
				
		return true;
	}
	


}
