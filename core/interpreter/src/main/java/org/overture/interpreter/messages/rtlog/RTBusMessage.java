package org.overture.interpreter.messages.rtlog;

import org.overture.interpreter.scheduler.MessagePacket;

public abstract class RTBusMessage extends RTMessage
{
	public MessagePacket message;

	public RTBusMessage(MessagePacket message)
	{
		this.message = message;
	}

}
