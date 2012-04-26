package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.MessagePacket;

public class RTBusCompletedMessage extends RTBusMessage
{

	public RTBusCompletedMessage(MessagePacket message)
	{
		super(message);
	}

	@Override
	String getInnerMessage()
	{
		return "MessageCompleted -> msgid: " + message.msgId;
	}

}
