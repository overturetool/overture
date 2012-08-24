package org.overture.ide.plugins.showtraceNextGen.view;

import java.io.IOException;

import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

public class NextGenTraceParser extends Thread {
	
	private String fileName = null;
	private boolean isFinished = false;
	private Object lock = new Object();
	private NextGenRTLogger rtLogger = null;
	public TraceData data = null;
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
						
			data = new TraceData();
			data.sortEvents();
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
