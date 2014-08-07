package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenBusMessageEvent implements Serializable, INextGenEvent
{

	public enum NextGenBusMessageEventType
	{
		ACTIVATE, COMPLETED, REPLY_REQUEST, REQUEST
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7067965431526303338L;

	public NextGenBusMessage message;
	public NextGenBusMessageEventType type;
	public NextGenTimeStamp time;

	public NextGenBusMessageEvent(NextGenBusMessage message,
			NextGenBusMessageEventType type, NextGenTimeStamp time)
	{
		this.message = message;
		this.type = type;
		this.time = time;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		switch (type)
		{
			case ACTIVATE:
				s.append("MessageActivate ->");
				s.append(" msgid: ");
				s.append(message.id);
				s.append(" time: ");
				s.append(time.toString());
				break;
			case COMPLETED:
				s.append("MessageCompleted ->");
				s.append(" msgid: ");
				s.append(message.id);
				s.append(" time: ");
				s.append(time.toString());
				break;
			case REQUEST:
				s.append("MessageRequest ->");
				s.append(" busid: ");
				s.append(this.message.bus.id);
				s.append(" fromcpu: ");
				s.append(this.message.fromCpu.id);
				s.append(" tocpu: ");
				s.append(this.message.toCpu.id);
				s.append(" msgid: ");
				s.append(this.message.id);
				s.append(" callthr: ");
				s.append(this.message.callerThread.id);
				s.append(" opname: \"");
				s.append(this.message.operation.name);
				s.append("\"");
				s.append(" objref: ");
				s.append(this.message.object.id);
				s.append(" size: ");
				s.append(this.message.size);
				s.append(" time: ");
				s.append(time.toString());
				break;
			default:
				break;
		}

		return s.toString();
	}

	public NextGenTimeStamp getTime()
	{
		return time;
	}

}
