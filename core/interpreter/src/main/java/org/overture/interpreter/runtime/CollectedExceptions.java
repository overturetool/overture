package org.overture.interpreter.runtime;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class CollectedExceptions extends RuntimeException implements
		ICollectedRuntimeExceptions
{
	public final List<Exception> exceptions;

	public CollectedExceptions(List<Exception> exception)
	{
		this.exceptions = exception;
	}

	public CollectedExceptions(Set<ContextException> problems)
	{
		this(new Vector<>(problems));
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
		return exceptions.get(0).getMessage();
	}

	@Override
	public String toString()
	{
		return exceptions.get(0).toString();
	}
}
