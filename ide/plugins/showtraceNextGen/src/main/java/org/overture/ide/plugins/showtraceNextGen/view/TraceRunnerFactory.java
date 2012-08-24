package org.overture.ide.plugins.showtraceNextGen.view;

import org.overture.ide.plugins.showtraceNextGen.data.TraceData;

public class TraceRunnerFactory {
	public static ITraceRunner getTraceRunner(TraceData data)
	{
		//return new TracefileVisitor(data);
		return new TraceFileRunner(data);
	}
}
