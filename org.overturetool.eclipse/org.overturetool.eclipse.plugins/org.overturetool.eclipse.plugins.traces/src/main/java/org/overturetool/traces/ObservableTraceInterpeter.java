package org.overturetool.traces;

import org.eclipse.core.runtime.IProgressMonitor;
import org.overturetool.traces.vdmj.TraceInterpreter;

public class ObservableTraceInterpeter extends TraceInterpreter
{
	IProgressMonitor monitor;
	Integer worked;
	VdmjTracesHelper console;
	public ObservableTraceInterpeter(IProgressMonitor monitor,VdmjTracesHelper console)
	{
		super();
		this.monitor=monitor;
		this.console=console;
	}
	
	
	protected void processingClass(String className, Integer traceCount)
	{
//		monitor.beginTask("Executing: " + className+ " - Trace count: " +traceCount,traceCount);
//		worked=0;
		console.ConsolePrint("Executing: " + className+ " - Trace count: " +traceCount);
	}
	
	protected void processingTrace(String className, String traceName,Integer testCount)
	{
		monitor.beginTask("Executing: " + className+ " - " + traceName +" - Trace count: " +testCount,testCount);
		monitor.worked(0);
		
	}
	protected void processingTest(String className, String traceName,Integer testNumber)
	{
		monitor.worked(testNumber);
	}
	
	protected void completed()
	{
		monitor.done();
	}
	
	protected void error(String message)
	{
		console.ConsolePrint(message);

	}
	
	protected void typeError(String message)
	{
		console.ConsolePrint(message);

	}

	
}
