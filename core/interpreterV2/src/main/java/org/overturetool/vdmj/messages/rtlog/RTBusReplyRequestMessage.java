package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.MessageResponse;

public class RTBusReplyRequestMessage extends RTBusMessage
{

	public RTBusReplyRequestMessage(MessageResponse response)
	{
		super(response);
	}

	@Override
	String getInnerMessage()
	{
		return "ReplyRequest -> busid: " + message.bus.getNumber() +
		" fromcpu: " + message.from.getNumber() +
		" tocpu: " + message.to.getNumber() +
		" msgid: " + message.msgId +
		" origmsgid: " + ((MessageResponse)message).originalId +
		" callthr: " + ((MessageResponse)message).caller.getId() +
		" calleethr: " + message.thread.getId() +
		" size: " + ((MessageResponse)message).getSize();
	}

}
