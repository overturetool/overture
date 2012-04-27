package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.MessagePacket;

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
