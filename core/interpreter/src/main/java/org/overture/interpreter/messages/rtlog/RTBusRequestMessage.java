package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.MessageRequest;

public class RTBusRequestMessage extends RTBusMessage
{

	public RTBusRequestMessage(MessageRequest request)
	{
		super(request);
	}

	@Override
	String getInnerMessage()
	{
		return "MessageRequest -> busid: " + message.bus.getNumber()
				+ " fromcpu: " + message.from.getNumber() + " tocpu: "
				+ message.to.getNumber() + " msgid: " + message.msgId
				+ " callthr: " + message.thread.getId() + " opname: " + "\""
				+ message.operation.name + "\"" + " objref: "
				+ message.target.objectReference + " size: "
				+ ((MessageRequest) message).getSize();
	}

}
