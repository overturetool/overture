package org.overture.interpreter.messages.rtlog.nextgen;

public class NextGenBusMessageReplyRequestEvent extends NextGenBusMessageEvent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 399996137218835546L;

	public NextGenBusMessage replyMessage;

	public NextGenBusMessageReplyRequestEvent(
			NextGenBusMessage originalMessage, NextGenBusMessage replyMessage,
			NextGenTimeStamp time)
	{
		super(originalMessage, NextGenBusMessageEventType.REPLY_REQUEST, time);
		this.replyMessage = replyMessage;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("ReplyRequest ->");
		s.append(" busid: ");
		s.append(this.replyMessage.bus.id);
		s.append(" fromcpu: ");
		s.append(this.replyMessage.fromCpu.id);
		s.append(" tocpu: ");
		s.append(this.replyMessage.toCpu.id);
		s.append(" msgid: ");
		s.append(this.replyMessage.id);
		s.append(" origmsgid: ");
		s.append(this.message.id);
		s.append(" callthr: ");
		s.append(this.message.callerThread.id);
		s.append(" calleethr: ");
		s.append(this.replyMessage.callerThread.id);
		s.append(" size: ");
		s.append(this.replyMessage.size);
		s.append(" time: ");
		s.append(time.toString());

		return s.toString();
	}

}
