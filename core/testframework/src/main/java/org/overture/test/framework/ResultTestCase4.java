/*
 * #%~
 * Test Framework for Overture
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
package org.overture.test.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.IResultCombiner;
import org.overture.test.framework.results.Result;
import org.overture.test.util.XmlResultReaderWriter;
import org.overture.test.util.XmlResultReaderWriter.IResultStore;

public abstract class ResultTestCase4<R> implements IResultStore<R>
{
	protected File file;

	public ResultTestCase4()
	{
		super();
	}

	public ResultTestCase4(File file)
	{
		this.file = file;
	}

	protected void compareResults(Result<R> result, String filename)
	{
		if (Properties.recordTestResults)
		{
			File resultFile = createResultFile(filename);
			resultFile.getParentFile().mkdirs();
			XmlResultReaderWriter<R> xmlResult = new XmlResultReaderWriter<R>(resultFile, this);
			xmlResult.setResult(this.getClass().getName(), result);
			try
			{
				xmlResult.saveInXml();
			} catch (ParserConfigurationException e)
			{
				throw new RuntimeException("Failed to encode recorded test result xml", e);
			} catch (TransformerException e)
			{
				throw new RuntimeException("Failed to transform recorded test result xml", e);
			}

			return;
		}

		File file = getResultFile(filename);

		Assert.assertNotNull("Result file " + filename + " was not found", file);
		Assert.assertTrue("The result files does not exist: " + file.getPath()
				+ "\n\n Cannot compare result:\n " + result, file.exists());
		if (!file.exists())
		{
			// Assume doesn't always work.
			return;
		}
		Assert.assertTrue("Result file " + file.getAbsolutePath()
				+ " does not exist", file.exists());

		// MessageReaderWriter mrw = new MessageReaderWriter(file);
		XmlResultReaderWriter<R> xmlResult = new XmlResultReaderWriter<R>(file, this);
		boolean parsed = xmlResult.loadFromXml();

		Assert.assertTrue("Could not read result file: " + file.getName(), parsed);

		if (parsed)
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(os);
			boolean errorsFound = checkMessages("warning", xmlResult.getWarnings(), result.warnings, pw);
			errorsFound = checkMessages("error", xmlResult.getErrors(), result.errors, pw)
					|| errorsFound;
			errorsFound = !assertEqualResults(xmlResult.getResult().result, result.result, pw)
					|| errorsFound;
			pw.flush();
			pw.close();
			Assert.assertFalse("Errors found in file \"" + filename + "\"\n\n"
					+ os.toString(), errorsFound);
		}
	}

	/**
	 * Checks if the results are equal.
	 * 
	 * @param expected
	 *            The expected result
	 * @param actual
	 *            The actual result
	 * @return If equal true or check has to be ignored true is returned else false
	 */
	protected abstract boolean assertEqualResults(R expected, R actual,
			PrintWriter out);

	protected abstract File createResultFile(String filename);

	protected abstract File getResultFile(String filename);

	public boolean checkMessages(String typeName, List<IMessage> expectedList,
			List<IMessage> list, PrintWriter out)
	{
		String TypeName = typeName.toUpperCase().toCharArray()[0]
				+ typeName.substring(1);
		boolean errorFound = false;
		for (IMessage w : list)
		{
			boolean isContainedIn = containedIn(expectedList, w);
			if (!isContainedIn)
			{
				out.println(padRight("Unexpected " + TypeName + ": ", 20) + w);
				errorFound = true;
			}

			// assertTrue(TypeName + " not expected: " + w, isContainedIn);
		}
		for (IMessage w : expectedList)
		{
			boolean isContainedIn = containedIn(list, w);
			if (!isContainedIn)
			{
				out.println(padRight("Missing " + TypeName + ": ", 20) + w);
				errorFound = true;
			}
			// assertTrue(TypeName + " expected but not found: " + w, isContainedIn);
		}
		return errorFound;
	}

	private static boolean containedIn(List<IMessage> list, IMessage m)
	{
		for (IMessage m1 : list)
		{
			if (m1.equals(m))
			{
				return true;
			}
		}
		return false;
	}

	private String padRight(String text, int length)
	{
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}

	protected <T> Result<T> mergeResults(Set<? extends Result<T>> parse,
			IResultCombiner<T> c)
	{
		List<IMessage> warnings = new ArrayList<IMessage>();
		List<IMessage> errors = new ArrayList<IMessage>();
		T result = null;

		for (Result<T> r : parse)
		{
			warnings.addAll(r.warnings);
			errors.addAll(r.errors);
			result = c.combine(result, r.result);
		}
		return new Result<T>(result, warnings, errors);
	}

}
