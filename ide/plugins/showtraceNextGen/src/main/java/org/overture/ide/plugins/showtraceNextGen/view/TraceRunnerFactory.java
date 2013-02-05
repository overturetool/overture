package org.overture.ide.plugins.showtraceNextGen.view;

import org.overture.ide.plugins.showtraceNextGen.data.ConjectureData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceData;

public class TraceRunnerFactory {
	public static ITraceRunner getTraceRunner(TraceData data, ConjectureData conjectures)
	{
		//return new TracefileVisitor(data);
		return new TraceFileRunner(data, conjectures);
	}
}
