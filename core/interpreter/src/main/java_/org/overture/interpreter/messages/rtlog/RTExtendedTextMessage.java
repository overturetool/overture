package org.overture.interpreter.messages.rtlog;

public class RTExtendedTextMessage extends RTExtendedMessage
{
	final String text;
	public RTExtendedTextMessage(String text)
	{
		this.text = text;
	}

	@Override
	String getInnerMessage()
	{
		return text;
	}

}
