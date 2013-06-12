package org.overture.codegen.logging;

public class Logger
{
	
	private static ILogger log;
	
	private Logger()
	{
	}
	
	public static void setLog(ILogger newLog)
	{
		log = newLog;
	}
	
	public static ILogger getLog()
	{
		if(log == null)
			log = DefaultLogger.getDefaultLogger();
		
		return log;
	}
	
}
