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

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent.NextGenBusMessageEventType;

public class CPUEventFilter extends EventFilter {

	private final Long cpuId;
	public CPUEventFilter(Long cpuId) {
		this.cpuId = cpuId;
	}

	@Override
	public boolean apply(INextGenEvent event) {
		//First apply filter of super class
		if(!super.apply(event))
			return false;
		
		boolean isForThisCpu = false;

		if(event instanceof NextGenThreadEvent) {
			int eventCpu = ((NextGenThreadEvent)event).thread.cpu.id;
			if(eventCpu == cpuId) {
				isForThisCpu = true;
			}
		}
		else if(event instanceof NextGenOperationEvent) {
			int operationCpuId = ((NextGenOperationEvent)event).thread.cpu.id;
			isForThisCpu = (operationCpuId == cpuId.intValue());			
		}
		else if(event instanceof NextGenBusMessageReplyRequestEvent) {
			int fromCpu = ((NextGenBusMessageReplyRequestEvent)event).replyMessage.fromCpu.id;
			isForThisCpu = (fromCpu == cpuId.intValue());
		}
		else if(event instanceof NextGenBusMessageEvent) {
			NextGenBusMessageEvent busMsg = (NextGenBusMessageEvent)event;
			if(busMsg.type != NextGenBusMessageEventType.ACTIVATE) {
				int fromCpu = busMsg.message.fromCpu.id;
				int toCpu =  busMsg.message.toCpu.id;

				switch(busMsg.type) {
				case ACTIVATE: 		isForThisCpu = (fromCpu == cpuId); break;
				case COMPLETED: 	isForThisCpu = (toCpu == cpuId); break;
				case REQUEST: 		isForThisCpu = (fromCpu == cpuId); break;
				default: 			isForThisCpu = false;
				}
			}
		}
		else {
			throw new IllegalArgumentException("Unknown event type!");
		}

		return isForThisCpu;
	}

}
