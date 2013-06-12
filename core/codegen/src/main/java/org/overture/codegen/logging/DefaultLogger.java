package org.overture.codegen.logging;

public class DefaultLogger implements ILogger
{

	private static DefaultLogger log;
	
	public static DefaultLogger getDefaultLogger()
	{
		if(log == null)
			log = new DefaultLogger();
		
		return log;
	}
	
	
	private boolean silent;
	
	private DefaultLogger()
	{
		this.silent = false;
	}
	
	@Override
	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}
	
	@Override
	public void println(String msg)
	{
		if(silent)
			return;
		
		System.out.println(msg);
	}

	@Override
	public void print(String msg)
	{
		if(silent)
			return;
		
		System.out.print(msg);
	}

	@Override
	public void printErrorln(String msg)
	{
		if(silent)
			return;
		
		System.err.println(msg);
	}

	@Override
	public void printErrpr(String msg)
	{
		if(silent)
			return;
		
		System.err.print(msg);
	}
	
}
