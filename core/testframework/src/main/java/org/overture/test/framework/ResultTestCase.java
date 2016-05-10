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

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.IResultCombiner;
import org.overture.test.framework.results.Result;
import org.overture.test.util.XmlResultReaderWriter;
import org.overture.test.util.XmlResultReaderWriter.IResultStore;

public abstract class ResultTestCase<R> extends BaseTestCase implements
		IResultStore<R>
{
	public ResultTestCase()
	{
		super();
	}

	public ResultTestCase(File file)
	{
		super(file);
	}

	public ResultTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	protected void compareResults(Result<R> result, String filename)
	{
		if (Properties.recordTestResults)
		{
			// MessageReaderWriter mrw = new MessageReaderWriter(createResultFile(filename));
			// mrw.set(result);
			// mrw.save();
			File resultFile = createResultFile(filename);
			resultFile.getParentFile().mkdirs();
			XmlResultReaderWriter<R> xmlResult = new XmlResultReaderWriter<>(resultFile, this);
			xmlResult.setResult(this.getClass().getName(), result);
			try
			{
				xmlResult.saveInXml();
			} catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return;
		}

		File file = getResultFile(filename);

		assertNotNull("Result file " + file.getName() + " was not found", file);
		assertTrue("Result file " + file.getAbsolutePath() + " does not exist", file.exists());

		// MessageReaderWriter mrw = new MessageReaderWriter(file);
		XmlResultReaderWriter<R> xmlResult = new XmlResultReaderWriter<>(file, this);
		boolean parsed = xmlResult.loadFromXml();

		assertTrue("Could not read result file: " + file.getName(), parsed);

		if (parsed)
		{
			boolean errorsFound = checkMessages("warning", xmlResult.getWarnings(), result.warnings);
			errorsFound = checkMessages("error", xmlResult.getErrors(), result.errors)
					|| errorsFound;
			errorsFound = !assertEqualResults(xmlResult.getResult().result, result.result)
					|| errorsFound;
			assertFalse("Errors found in file \"" + filename + "\"", errorsFound);
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
	protected abstract boolean assertEqualResults(R expected, R actual);

	protected abstract File createResultFile(String filename);

	protected abstract File getResultFile(String filename);

	public boolean checkMessages(String typeName, List<IMessage> expectedList,
			List<IMessage> list)
	{
		String TypeName = typeName.toUpperCase().toCharArray()[0]
				+ typeName.substring(1);
		boolean errorFound = false;
		for (IMessage w : list)
		{
			boolean isContainedIn = containedIn(expectedList, w);
			if (!isContainedIn)
			{
				System.out.println(TypeName + " not expected: " + w);
				errorFound = true;
			}

			// assertTrue(TypeName + " not expected: " + w, isContainedIn);
		}
		for (IMessage w : expectedList)
		{
			boolean isContainedIn = containedIn(list, w);
			if (!isContainedIn)
			{
				System.out.println(TypeName + " expected but not found: " + w);
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

	protected <T> Result<T> mergeResults(Set<? extends Result<T>> parse,
			IResultCombiner<T> c)
	{
		List<IMessage> warnings = new Vector<>();
		List<IMessage> errors = new Vector<>();
		T result = null;

		for (Result<T> r : parse)
		{
			warnings.addAll(r.warnings);
			errors.addAll(r.errors);
			result = c.combine(result, r.result);
		}
		return new Result<>(result, warnings, errors);
	}

}
