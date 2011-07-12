package com.lausdahl.ast.creator;

public class AstCreatorException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final boolean fatal;

	public AstCreatorException(String message, Throwable t, boolean fatal)
	{
		super(message, t);
		this.fatal = fatal;
	}
}
