package org.overture.ide.plugins.showtraceNextGen.viewer;

import java.io.IOException;

import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

public class NextGenTraceParser extends Thread {
	
	private String fileName = null;
	private boolean isFinished = false;
	private Object lock = new Object();
	public NextGenRTLogger rtLogger = null;
	public Exception error = null;

	public NextGenTraceParser(String file)
	{
		this.fileName = file;
	}
    
	@Override
	public void run()
	{
		try 
		{
			rtLogger = NextGenRTLogger.getInstanceFromFile(fileName);
		} 
		catch (IOException e) {
			error = e;
		} 
		catch (ClassNotFoundException e) {
			error = e;
		}
	
		synchronized (lock)
		{
			isFinished = true;
		}

	}

	public boolean isFinished()
	{
		return isFinished;
	}
}
