/*
 * #%~
 * Combinatorial Testing Utilities
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ct.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.traces.Verdict;

public class TraceXmlWrapper
{
	XmlFileWriter xml;

	public static final String CLASS_TAG = "class";
	public static final String TRACE_TAG = "trace";
	public static final String TEST_CASE_TAG = "test";
	public static final String RESULT_TAG = "result";
	public static final String STATEMENT_TAG = "statement";
	public static final String NAME_TAG = "name";
	public static final String NUMBER_TAG = "no";
	public static final String NUMBER_OF_TESTS_TAG = "testcount";
	public static final String VERDICT_TAG = "verdict";
	public static final String ROOT_TAG = "traces";

	public static final String FILE_NAME_TAG = "filename";
	public static final String START_LINE_TAG = "line";
	public static final String START_COL_TAG = "column";
	public static final String STATUS_TAG = "status";
	public static final String NUMBER_OF_SKIPPED_TESTS_TAG = "skippedcount";
	public static final String NUMBER_OF_FAILD_TESTS_TAG = "faildcount";
	public static final String NUMBER_OF_INCONCLUSIVE_TESTS_TAG = "inconclusivecount";
	public static final String SUBSET = "SUBSET".toLowerCase();
	public static final String TRACE_REDUCTION = "REDUCTION".toLowerCase();
	public static final String SEED = "SEED".toLowerCase();

	public TraceXmlWrapper(File file) throws IOException
	{
		xml = new XmlFileWriter();
		xml.startDocument(file, ROOT_TAG);
		// openElements.push(ROOT_TAG);
	}

	Stack<String> openElements = new Stack<>();

	public void StartClass(String className) throws IOException
	{
		xml.startElement(CLASS_TAG, NAME_TAG, className);
		openElements.push(CLASS_TAG);
	}

	public void StopElement() throws IOException
	{
		String element = openElements.pop();
		xml.stopElement(element);
	}

	public void StartTrace(String traceName, String fileName, Integer line,
			Integer col, Integer numberOfTests, Float subset,
			TraceReductionType traceReductionType, Long seed)
			throws IOException
	{
		xml.startElement(TRACE_TAG, NAME_TAG, traceName, NUMBER_OF_TESTS_TAG, numberOfTests.toString(), FILE_NAME_TAG, fileName, START_LINE_TAG, line.toString(), START_COL_TAG, col.toString(), SUBSET, subset.toString(), TRACE_REDUCTION, traceReductionType.toString(), SEED, seed.toString());
		openElements.push(TRACE_TAG);
	}

	public void StartTest(String testName, String statement) throws IOException
	{
		xml.startElement(TEST_CASE_TAG, NUMBER_TAG, testName);
		xml.writeValue(statement);
		openElements.push(TEST_CASE_TAG);
	}

	public void AddResults(String testName, List<Object> results)
			throws IOException
	{

		String result = "";
		String verdict = "";
		for (Object object : results)
		{
			if (object instanceof Verdict)
			{
				verdict = object.toString();
			} else
			{
				if (object == null)
				{
					result += "null";
				} else
				{
					result += object.toString();
				}
				result += " ; ";
			}
		}

		if (result.length() > 3)
		{
			result = result.substring(0, result.length() - 3); // remove the
																// last ;
		}
		xml.startElement(RESULT_TAG, NUMBER_TAG, testName, VERDICT_TAG, verdict);
		xml.writeValue(result);
		xml.stopElement(RESULT_TAG);
	}

	public void AddTraceStatus(Verdict worstVerdict, Integer totalCountOfTests,
			Integer skippedTests, Integer faildTests, Integer inconclusive)
			throws IOException
	{
		xml.startElement(STATUS_TAG, VERDICT_TAG, worstVerdict.toString(), NUMBER_OF_TESTS_TAG, totalCountOfTests.toString(), NUMBER_OF_SKIPPED_TESTS_TAG, skippedTests.toString(), NUMBER_OF_FAILD_TESTS_TAG, faildTests.toString(), NUMBER_OF_INCONCLUSIVE_TESTS_TAG, inconclusive.toString());
		xml.stopElement(STATUS_TAG);
	}

	public void Stop() throws IOException
	{
		while (!openElements.isEmpty())
		{
			xml.stopElement(openElements.pop());
		}
		xml.stopDocument();

	}

	public void AddSkippedResult(String testName) throws IOException
	{
		xml.startElement(RESULT_TAG, NUMBER_TAG, testName, VERDICT_TAG, Verdict.SKIPPED.toString());
		xml.stopElement(RESULT_TAG);

	}

}
