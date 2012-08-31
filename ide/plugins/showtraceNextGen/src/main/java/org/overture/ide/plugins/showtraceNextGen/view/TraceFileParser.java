package org.overture.ide.plugins.showtraceNextGen.view;

import java.io.IOException;

import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;

public class TraceFileParser extends Thread {
	
	private String fileName = null;
	private boolean isFinished = false;
	private Object lock = new Object();
	public TraceData data = null;
	public Exception error = null;

	public TraceFileParser(String file)
	{
		this.fileName = file;
	}
    
	@Override
	public void run()
	{
		try 
		{
			NextGenRTLogger logger = NextGenRTLogger.getInstanceFromFile(fileName);
			data = new TraceData(logger);

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
