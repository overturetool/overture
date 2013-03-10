package org.overture.ide.plugins.showtraceNextGen.event;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent.NextGenBusMessageEventType;

public class CPUEventFilter implements EventFilter {

	private final Long cpuId;
	public CPUEventFilter(Long cpuId) {
		this.cpuId = cpuId;
	}

	@Override
	public boolean apply(INextGenEvent event) {
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
