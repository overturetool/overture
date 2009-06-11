package org.overturetool.traces.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.xml.sax.SAXException;

public interface ITracesHelper
{

	// public abstract String[] GetTraceClasNames() throws Exception;

	// public abstract List<String> GetTraces(String className) throws
	// Exception;
	public abstract List<String> GetClassNamesWithTraces() throws IOException;

	public abstract List<NamedTraceDefinition> GetTraceDefinitions(
			String className) throws IOException,SAXException;

	public abstract List<TraceTestResult> GetTraceTests(String className,
			String trace) throws SAXException, IOException;

	public abstract List<TraceTestResult> GetTraceTests(String className,
			String trace, Integer startNumber, Integer stopNumber)
			throws SAXException, IOException;

	public abstract Integer GetTraceTestCount(String className, String trace)
			throws SAXException, IOException;

	// public abstract TraceTestStatus RunSingle(String className,
	// String traceName, Integer number) throws SAXException;

	public abstract void processSingleTrace(String className,
			String traceName,Object monitor) throws Exception;

	public abstract void processClassTraces(String className,
			Object monitor) throws Exception;

	public abstract TraceTestStatus GetStatus(String className, String trace,
			Integer num) throws SAXException, IOException;

	public abstract TraceTestResult GetResult(String className, String trace,
			Integer num) throws SAXException, IOException;

	// public abstract void SetFail(String className, String trace, Integer num)
	// throws CGException;
	//
	// public abstract void SetOk(String className, String trace, Integer num)
	// throws CGException;

	// public abstract boolean HasError(String className, String trace)
	// throws CGException;

	// public abstract List<TraceError> GetError(String className,
	// String trace) throws CGException;

	// public abstract void Save(String path) throws CGException ;

	public abstract File GetFile(String className) throws IOException;

	public abstract int GetSkippedCount(String className, String traceName)
			throws SAXException, IOException;

	public enum TestResultType
	{
		Ok, Fail, Inconclusive, Unknown, Skipped
	}
	// ExpansionFaild,
}
