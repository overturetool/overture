package org.overturetool.vdmj.messages.rtlog;

import org.overturetool.vdmj.scheduler.MessagePacket;

public abstract class RTBusMessage extends RTMessage
{
	protected MessagePacket message;

	public RTBusMessage(MessagePacket message)
	{
		this.message = message;
	}
	
}
