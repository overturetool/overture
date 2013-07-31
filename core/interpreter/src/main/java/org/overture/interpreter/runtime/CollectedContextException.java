package org.overture.interpreter.runtime;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class CollectedContextException extends ContextException implements ICollectedRuntimeExceptions
{
	public final List<Exception> exceptions;

	public CollectedContextException(ContextException e,
			List<Exception> exceptions)
	{
		super(e.number, e.getMessage(), e.location, e.ctxt);
		this.exceptions = exceptions;
	}

	
	public CollectedContextException(ContextException toThrow,
			Set<ContextException> problems)
	{
		this(toThrow,new Vector<Exception>(problems));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.overture.interpreter.runtime.ICollectedRuntimeExceptions#getCollectedExceptions()
	 */
	@Override
	public List<Exception> getCollectedExceptions()
	{
		return this.exceptions;
	}

}
