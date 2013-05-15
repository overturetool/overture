package org.overture.codegen.logging;

public class DefaultLogger implements ILogger
{

	@Override
	public void println(String msg)
	{
		System.out.println(msg);
	}

	@Override
	public void print(String msg)
	{
		System.out.print(msg);
	}

	@Override
	public void printErrorln(String msg)
	{
		System.err.println(msg);
	}

	@Override
	public void printErrpr(String msg)
	{
		System.err.print(msg);
	}
	
}
