package org.overture.interpreter.runtime;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

public class CollectedContextException extends ContextException implements
		ICollectedRuntimeExceptions
{
	public final List<Exception> exceptions;
	String message = "";

	public CollectedContextException(ContextException e,
			List<Exception> exceptions)
	{
		super(e.number, e.getMessage(), e.location, e.ctxt);
		message = e.getMessage();
		this.exceptions = exceptions;
	}

	public CollectedContextException(ContextException toThrow,
			Set<ContextException> problems)
	{
		this(toThrow, new ArrayList<Exception>(problems));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see org.overture.interpreter.runtime.ICollectedRuntimeExceptions#getCollectedExceptions()
	 */
	@Override
	public List<Exception> getCollectedExceptions()
	{
		return this.exceptions;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
