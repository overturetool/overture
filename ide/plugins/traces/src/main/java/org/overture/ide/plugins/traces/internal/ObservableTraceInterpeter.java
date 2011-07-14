package org.overture.ide.plugins.traces.internal;

import java.util.concurrent.CancellationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.overturetool.traces.vdmj.TraceInterpreter;

public class ObservableTraceInterpeter extends TraceInterpreter
{
	IProgressMonitor monitor;
	VdmjTracesHelper console;

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
		monitor.subTask("Evaluating tests");
		console.consolePrint("Executing: " + className + " - Trace count: "
				+ traceCount);
	}

	@Override
	protected void preProcessingTrace(String className, String traceName,
			Integer testCount)
	{
		workedUnit = testCount.doubleValue() / 100.0;
		int worked = 0;

		monitor.beginTask("Executing: " + className + " - " + traceName
				+ " - Trace count: " + testCount, 100);
		monitor.worked(worked);

		console.consolePrint(className + " - " + traceName + " Test count = "
				+ testCount);

	}

	@Override
	protected void processingTest(String className, String traceName,
			Integer testNumber)
	{
		testCounter++;
		if (testCounter >= workedUnit)
		{
			double incrementBy=100.0/(workedUnit*100);
			monitor.worked((int)Math.round( incrementBy));
			testCounter = 0;
		}
		if (monitor.isCanceled())
			throw new CancellationException(
					"Trace execution has been cancelled");
	}

	@Override
	protected void preCompleted()
	{
		long endClass = System.currentTimeMillis();
		console.consolePrint("Class " + activeClass + " processed in "
				+ (double) (endClass - beginClass) / 1000 + " secs");
	}
	@Override
	protected void prePrintTraceStatus()
	{
		if (super.activeTrace != null && super.beginTrace != 0)
		{
			long endTrace = System.currentTimeMillis();
			console.consolePrint("Trace " + activeClass + " - " + activeTrace
					+ " processed in " + (double) (endTrace - beginTrace)
					/ 1000 + " secs");
		}
	}

	@Override
	protected void error(String message)
	{
		console.consolePrint(message);
	}

	@Override
	protected void typeError(String message)
	{
		console.consolePrint(message);
	}

	@Override
	protected void typeCheckStarted()
	{
		monitor.subTask("Type checking");
	}

}
