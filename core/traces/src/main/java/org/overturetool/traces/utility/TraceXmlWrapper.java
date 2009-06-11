package org.overturetool.traces.utility;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.overturetool.vdmj.traces.Verdict;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;

public class TraceXmlWrapper
{
	XmlFileWriter xml;

	private String rootPath;
	public static final String CLASS_TAG = "Class";
	public static final String TRACE_TAG = "Trace";
	public static final String TEST_CASE_TAG = "Test";
	public static final String RESULT_TAG = "Result";
	public static final String STATEMENT_TAG = "Statement";
	public static final String NAME_TAG = "Name";
	public static final String NUMBER_TAG = "No";
	public static final String NUMBER_OF_TESTS_TAG = "TestCount";
	public static final String VERDICT_TAG = "Verdict";
	public static final String ROOT_TAG = "Traces";

	public static final String FILE_NAME_TAG = "Filename";
	public static final String START_LINE_TAG = "Line";
	public static final String START_COL_TAG = "Column";
	public static final String STATUS_TAG = "Status";
	public static final String NUMBER_OF_SKIPPED_TESTS_TAG = "SkippedCount";
	public static final String NUMBER_OF_FAILD_TESTS_TAG = "FaildCount";
	public static final String NUMBER_OF_INCONCLUSIVE_TESTS_TAG = "InconclusiveCount";

	public TraceXmlWrapper(String fileName) throws IOException
	{
		xml = new XmlFileWriter();
		xml.StartDocument(fileName, ROOT_TAG);
		// openElements.push(ROOT_TAG);
	}

	Stack<String> openElements = new Stack<String>();

	public void StartClass(String className)
	{
		xml.StartElement(CLASS_TAG, NAME_TAG, className);
		openElements.push(CLASS_TAG);
	}

	public void StopElement()
	{
		String element = openElements.pop();
		xml.StopElement(element);
	}

	public void StartTrace(String traceName, String fileName, Integer line,
			Integer col, Integer numberOfTests)
	{
		xml.StartElement(
				TRACE_TAG,
				NAME_TAG,
				traceName,
				NUMBER_OF_TESTS_TAG,
				numberOfTests.toString(),
				FILE_NAME_TAG,
				fileName,
				START_LINE_TAG,
				line.toString(),
				START_COL_TAG,
				col.toString());
		openElements.push(TRACE_TAG);
	}

	public void StartTest(String testName, String statement)
	{
		xml.StartElement(TEST_CASE_TAG, NUMBER_TAG, testName);
		xml.WriteValue(statement);
		openElements.push(TEST_CASE_TAG);
	}

	public void AddResults(String testName, List<Object> results)
	{

		String result = "";
		String verdict = "";
		for (Object object : results)
		{
			if (object instanceof Verdict)
				verdict = object.toString();
			else if (object instanceof Value)
				result += object.toString() + " ; ";
			else
				result += object.toString();

		}

		if (result.length() > 3)
			result = result.substring(0, result.length() - 3); // remove the
																// last ;

		xml.StartElement(RESULT_TAG, NUMBER_TAG, testName, VERDICT_TAG, verdict);
		xml.WriteValue(result);
		xml.StopElement(RESULT_TAG);
	}

	public void AddTraceStatus(Verdict worstVerdict, Integer totalCountOfTests,
			Integer skippedTests, Integer faildTests,Integer inconclusive)
	{
		xml.StartElement(
				STATUS_TAG,
				VERDICT_TAG,
				worstVerdict.toString(),
				NUMBER_OF_TESTS_TAG,
				totalCountOfTests.toString(),
				NUMBER_OF_SKIPPED_TESTS_TAG,
				skippedTests.toString(),
				NUMBER_OF_FAILD_TESTS_TAG,
				faildTests.toString(),
				NUMBER_OF_INCONCLUSIVE_TESTS_TAG,
				inconclusive.toString());
		xml.StopElement(STATUS_TAG);
	}

	public void Stop() throws IOException
	{
		while (!openElements.isEmpty())
			xml.StopElement(openElements.pop());
		xml.StopDocument();

	}

	public void AddSkippedResult(String testName)
	{
		xml.StartElement(
				RESULT_TAG,
				NUMBER_TAG,
				testName,
				VERDICT_TAG,
				"SKIPPED");
		xml.StopElement(RESULT_TAG);

	}

}
