/*
 * #%~
 * RT Trace Viewer Plugin
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadSwapEvent;

public class ThreadSwapEventHandler extends EventHandler {

	public ThreadSwapEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) 
	{
		NextGenThreadSwapEvent tEvent = null;
		
		if(event instanceof NextGenThreadSwapEvent)
			tEvent = (NextGenThreadSwapEvent)event;
		else
			throw new IllegalArgumentException("ThreadSwapEventHandler expected event of type: " + NextGenThreadSwapEvent.class.getName());
		
		Long cpuId = new Long(tEvent.thread.cpu.id);
		Long threadId = new Long(tEvent.thread.id);
		TraceCPU cpu = data.getCPU(cpuId);
		TraceThread swappedThread = data.getThread(threadId);
		
		TraceThread currentThread = null;
		Long currentThreadId = cpu.getCurrentThread();
		if(currentThreadId != null)
		{
			currentThread = data.getThread(currentThreadId);
		}
				
		switch(tEvent.swapType)
		{
			case SWAP_IN: 
				eventViewer.drawThreadSwapIn(tab, cpu, currentThread, swappedThread); 
				cpu.setCurrentThread(threadId);
				cpu.setIdle(false);
				break;
			case DELAYED_IN: 
				eventViewer.drawDelayedThreadSwapIn(tab, cpu, currentThread, swappedThread); 
				cpu.setCurrentThread(threadId);
				cpu.setIdle(false);
				break;
			case SWAP_OUT: 
				eventViewer.drawThreadSwapOut(tab, cpu, currentThread, swappedThread); 
				cpu.setCurrentThread(null);
				cpu.setIdle(true);
				break;
		}
				
		return;
	}
	


}
