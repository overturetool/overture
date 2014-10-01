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

		Long cpuId = new Long(tEvent.thread.cpu.id);
		Long newThreadId = new Long(tEvent.thread.id);
		Long objectId = null;
		TraceCPU cpu = data.getCPU(cpuId);
		
		TraceThread currentThread = null;
		Long currentThreadId = cpu.getCurrentThread();
		if(currentThreadId != null)
		{
			currentThread = data.getThread(currentThreadId);
		}
		
		TraceThread affectedThread = data.getThread(newThreadId);
		TraceObject object = null;
		
		switch(tEvent.type)
		{
		case CREATE: 			
			if(tEvent.thread.object == null)
			{
				if(tEvent.thread.type == ThreadType.MAIN) {
					object = data.getMainThreadObject();
				}
				else if(tEvent.thread.type == ThreadType.INIT) {
					object = data.getInitThreadObject();
				}
				else {
					throw new UnexpectedEventTypeException("Did not expect create event in ThreadEventHandler for other thread types than init and main at this point.");
				}
			}
			else
			{
				objectId = new Long(tEvent.thread.object.id);
				object = data.getObject(objectId);
			}
			affectedThread.pushCurrentObject(object);
			eventViewer.drawThreadCreate(tab, cpu, currentThread, affectedThread);
			break;
		case SWAP: 
			throw new UnexpectedEventTypeException("Problem in ThreadEventHandler. SWAP events should be handled in " + ThreadSwapEventHandler.class.getName());
		case KILL: 
			eventViewer.drawThreadKill(tab, cpu, currentThread, affectedThread);
			if(affectedThread.hasCurrentObject())
				affectedThread.popCurrentObject();
			break;
		}
	}
	

}
