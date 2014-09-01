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
