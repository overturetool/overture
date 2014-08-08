package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.MessagePacket;

public class RTBusActivateMessage extends RTBusMessage
{

	public RTBusActivateMessage(MessagePacket message)
	{
		super(message);
	}

	@Override
	String getInnerMessage()
	{
		return "MessageActivate -> msgid: " + message.msgId;
	}

}
