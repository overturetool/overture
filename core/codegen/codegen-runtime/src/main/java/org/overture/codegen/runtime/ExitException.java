package org.overture.codegen.runtime;

public class ExitException extends RuntimeException
{
	private static final long serialVersionUID = 8786789051020364604L;

	public ExitException()
	{
	}

	public ExitException(Object arg)
	{
		super(Utils.toString(arg));
	}
}
