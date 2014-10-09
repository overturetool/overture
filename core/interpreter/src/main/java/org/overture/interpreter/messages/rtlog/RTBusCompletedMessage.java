package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.MessagePacket;

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
