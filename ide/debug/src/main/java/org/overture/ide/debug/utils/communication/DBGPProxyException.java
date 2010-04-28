package org.overture.ide.debug.utils.communication;

public class DBGPProxyException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6934604897934758206L;
	public final Integer threadId;

	public DBGPProxyException(Exception internalException, Integer threadId)
	{
		super(internalException);
		this.threadId = threadId;
	}
	
	@Override
	public String toString()
	{
		return "Debug Thread id: "+threadId+"\n"+super.toString();
	}

}
