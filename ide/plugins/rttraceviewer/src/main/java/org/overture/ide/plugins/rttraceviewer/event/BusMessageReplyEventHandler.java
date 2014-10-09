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

import org.overture.ide.plugins.rttraceviewer.data.TraceBus;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceOperation;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;

public class BusMessageReplyEventHandler extends EventHandler {

	public BusMessageReplyEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
		if(!(event instanceof NextGenBusMessageReplyRequestEvent))
			throw new IllegalArgumentException("BusMessageReplyEventHandler expected event of type: " + NextGenBusMessageReplyRequestEvent.class.getName());
		
		NextGenBusMessageReplyRequestEvent bEvent = (NextGenBusMessageReplyRequestEvent)event;;

		//TraceBusMessage msg = data.getMessage(bEvent.replyMessage.id);
		TraceCPU fromCpu = data.getCPU(new Long(bEvent.replyMessage.fromCpu.id));
		TraceThread fromThread = data.getThread(new Long(bEvent.replyMessage.callerThread.id));
		TraceObject object = fromThread.getCurrentObject();	
		TraceBus bus = data.getBUS(new Long(bEvent.replyMessage.bus.id));
		TraceOperation op = data.getOperation(bEvent.replyMessage.operation.classDef.name + bEvent.replyMessage.operation.name);
				
		eventViewer.drawReplyRequest(tab,  fromCpu, object, bus, op);
		return;
	}


}
