package org.overturetool.traces;

import java.util.concurrent.CancellationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.overturetool.traces.vdmj.TraceInterpreter;

public class ObservableTraceInterpeter extends TraceInterpreter
{
	IProgressMonitor monitor;
	VdmjTracesHelper console;
	// long beginClass = 0;
	// long beginTrace = 0;
	// String activeClass = "";
	// String activeTrace;
	// int worked;
	double workedUnit;
	long testCounter;

	public ObservableTraceInterpeter(IProgressMonitor monitor,
			VdmjTracesHelper console)
	{
		super();
		this.monitor = monitor;
		this.console = console;
	}

	@Override
	protected void preProcessingClass(String className, Integer traceCount)
	{
		// beginClass = System.currentTimeMillis();
		// activeClass = className;
		// monitor.beginTask("Executing: " + className+ " - Trace count: "
		// +traceCount,traceCount);
		// worked=0;
		monitor.subTask("Evaluating tests");
		console.ConsolePrint("Executing: " + className + " - Trace count: "
				+ traceCount);
	}

	@Override
	protected void preProcessingTrace(String className, String traceName,
			Integer testCount)
	{
		
		// beginTrace = System.currentTimeMillis();
		// activeTrace = traceName;

		workedUnit = testCount.doubleValue() / 100;
		int worked = 0;

		monitor.beginTask("Executing: " + className + " - " + traceName
				+ " - Trace count: " + testCount, 100);
		monitor.worked(worked);

		console.ConsolePrint(className + " - " + traceName + " Test count = "
				+ testCount);

	}

	@Override
	protected void processingTest(String className, String traceName,
			Integer testNumber)
	{
		testCounter++;
		if (testCounter >= workedUnit)
		{
			monitor.worked(1);
			// console.ConsolePrint("Worked: "+worked);
			testCounter = 0;
		}
		if (monitor.isCanceled())
			throw new CancellationException(
					"Trace execution has been cancelled");
		// console.ConsolePrint("Worked=" + testNumber);
		// console.ConsolePrint(className+"-"+traceName + "-" + testNumber);
	}

	@Override
	protected void preCompleted()
	{

		// printTraceStatus();

		long endClass = System.currentTimeMillis();
		console.ConsolePrint("Class " + activeClass + " processed in "
				+ (double) (endClass - beginClass) / 1000 + " secs");

		// monitor.done();
	}
	@Override
	protected void prePrintTraceStatus()
	{
		if (super.activeTrace != null && super.beginTrace != 0)
		{
			long endTrace = System.currentTimeMillis();
			console.ConsolePrint("Trace " + activeClass + " - " + activeTrace
					+ " processed in " + (double) (endTrace - beginTrace)
					/ 1000 + " secs");
		}
	}

	@Override
	protected void error(String message)
	{
		console.ConsolePrint(message);

	}

	@Override
	protected void typeError(String message)
	{
		console.ConsolePrint(message);

	}

	@Override
	protected void typeCheckStarted()
	{
		monitor.subTask("Type checking");
	}

}
