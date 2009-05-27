package org.overturetool.traces.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.overturetool.vdmj.traces.Verdict;
import org.overturetool.vdmj.values.TupleValue;

public class TraceXmlWrapper {
	XmlFileWriter xml;

	private String rootPath;
	final String CLASS_TAG = "Class";
	final String TRACE_TAG = "Trace";
	final String TEST_CASE_TAG = "TestCase";
	final String RESULT_TAG = "Result";
	final String STATEMENT_TAG = "Statement";
	final String NAME_TAG = "Name";
	final String NUMBER_TAG = "No";
	final String VERDICT_TAG = "Verdict";
	final String ROOT_TAG = "Traces";

	public TraceXmlWrapper(String fileName) throws IOException {
		xml = new XmlFileWriter();
		xml.StartDocument(fileName, ROOT_TAG);
		openElements.push(ROOT_TAG);
	}

	Stack<String> openElements = new Stack<String>();

	public void StartClass(String className) {
		xml.StartElement(CLASS_TAG, NAME_TAG, className);
		openElements.push(CLASS_TAG);
	}

	public void StopElement() {
		String element = openElements.pop();
		xml.StopElement(element);
	}

	public void StartTrace(String traceName, Integer line, Integer col) {
		xml.StartElement(TRACE_TAG, NAME_TAG, traceName);
		openElements.push(TRACE_TAG);
	}

	public void StartTest(String testName, String statement) {
		xml.StartElement(TEST_CASE_TAG, NUMBER_TAG, testName);
		xml.WriteValue(statement);
		openElements.push(TEST_CASE_TAG);
	}

	public void AddResults(String testName,List<Object> results) {

		String result = "";
		String verdict = "";
		for (Object object : results) {
			if (object instanceof Verdict)
				verdict = object.toString();
			else if (object instanceof TupleValue)
				result = object.toString();

		}

		xml.StartElement(RESULT_TAG, NUMBER_TAG,testName,VERDICT_TAG, verdict);
		xml.WriteValue(result);
		xml.StopElement(RESULT_TAG);
	}

	public void Stop() throws IOException {
		xml.StopDocument();
	}

}
