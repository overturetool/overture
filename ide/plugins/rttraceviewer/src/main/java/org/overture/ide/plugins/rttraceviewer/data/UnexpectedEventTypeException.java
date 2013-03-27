package org.overture.ide.plugins.rttraceviewer.data;

public class UnexpectedEventTypeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public UnexpectedEventTypeException(String message)
	{
		super(message);
	}
}
