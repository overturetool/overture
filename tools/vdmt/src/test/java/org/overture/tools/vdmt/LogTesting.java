package org.overture.tools.vdmt;

import org.apache.maven.plugin.logging.*;

public class LogTesting implements Log
{

	public void debug(CharSequence arg0)
	{
		System.out.println(arg0);
		
	}

	public void debug(Throwable arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void debug(CharSequence arg0, Throwable arg1)
	{
		// TODO Auto-generated method stub
		
	}

	public void error(CharSequence arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void error(Throwable arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void error(CharSequence arg0, Throwable arg1)
	{
		// TODO Auto-generated method stub
		
	}

	public void info(CharSequence arg0)
	{
		System.out.println(arg0);
		
	}

	public void info(Throwable arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void info(CharSequence arg0, Throwable arg1)
	{
		// TODO Auto-generated method stub
		
	}

	public boolean isDebugEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isErrorEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInfoEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWarnEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void warn(CharSequence arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void warn(Throwable arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public void warn(CharSequence arg0, Throwable arg1)
	{
		// TODO Auto-generated method stub
		
	}

}
