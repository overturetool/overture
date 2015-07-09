/*
 * #%~
 * Overture Testing Framework
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
package org.overture.core.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Top level class for new tests framework. Provides common result handling code to all other test classes. This class
 * should <b>not</b> be subclassed directly. Use one of its existing subclasses instead. Test results are always stored
 * with UTF-8 encoding.
 * 
 * @see ParamExamplesTest
 * @see ParamExternalsTest
 * @see ParamFineGrainTest
 * @see ParamStandardTest
 * @author ldc
 * @param <R>
 *            the (user-provided) type of results this test operates on
 */
public abstract class AbsResultTest<R>
{

	protected boolean updateResult;
	protected String resultPath;
	protected String testName;

	/**
	 * Deserialize test results. This method is capable of deserializing most results, provided the correct type
	 * information is provided via getResultType(). If your results are too complex for this method or if you are not
	 * using JSON to store then, them you must override the entire method.
	 * 
	 * @param resultPath
	 *            the file path to the stored result file
	 * @return the deserialized stored result
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public R deSerializeResult(String resultPath) throws FileNotFoundException,
			IOException
	{
		Gson gson = new Gson();
		Type resultType = getResultType();

		// check if exists
		File f = new File(resultPath);
		if (!f.exists())
		{
			f.getParentFile().mkdirs();
			throw new FileNotFoundException(resultPath);
		}

		InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(resultPath)), ParseTcFacade.UTF8);
		String json = IOUtils.toString(reader);
		R result = gson.fromJson(json, resultType);
		return result;
	}

	/**
	 * Calculate the type of the test result. This method must be overridden to provide the specific result type for
	 * each test. When doing so, you can use the snippet below (replacing <code>R</code> with the actual type of your
	 * result). Keep in mind this does not work for wildcards or type parameters. You <b>must</b> declare the actual
	 * type. <blockquote><code>
	 * Type resultType = new TypeToken< R >() {}.getType(); <br> 
	 * return resultType; 
	 * </blockquote></code>
	 * 
	 * @see TypeToken
	 * @return the {@link Type} of the result file
	 */
	abstract public Type getResultType();

	/**
	 * Return the Java System property to update this set of tests. Should have the following naming scheme:
	 * <code>tests.update.[module].[testId]</code>. <br>
	 * <br>
	 * The test ID <b>must</b> be unique to each test class. Module is just there to avoid name clashes so the name of
	 * the module is enough.
	 * 
	 * @return
	 */
	protected abstract String getUpdatePropertyString();

	/**
	 * Returns a message on how to update test results. Should be used in {@link #compareResults(Object, Object)}. * @return
	 * the result update message
	 */
	protected String getTestResultUpdateMessage()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Use -D\"");
		sb.append(getUpdatePropertyString());
		sb.append("\" to update the result file");

		return sb.toString();
	}

	/**
	 * Update the result file for this test. Result serialization is done with JSON and this should adequate for most
	 * users. If you need an alternative format, you may override this method.
	 * 
	 * @param actual
	 *            the new result to be saved
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 */
	protected void testUpdate(R actual) throws ParserException, LexException,
			IOException
	{
		Gson gson = new Gson();
		String json = gson.toJson(actual);

		// Make sure file can be created
		File f = new File(resultPath);
		if (!f.exists())
		{
			f.getParentFile().mkdirs();
		}

		IOUtils.write(json, new FileOutputStream(resultPath), ParseTcFacade.UTF8);
	}

	/**
	 * Check if test running in result update mode. This is done by consulting the update property as returned by {
	 * {@link #getUpdatePropertyString()}.
	 * 
	 * @return true if test is running in update mode. False otherwise
	 */
	protected boolean updateCheck()
	{
		String update_results_property = getUpdatePropertyString();

		// check update this test
		if (System.getProperty(update_results_property + "." + testName) != null)
		{
			return true;
		}

		// check update all
		if (System.getProperty(update_results_property) != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Compare output of the processed model with previously stored result. This method must be overridden to implement
	 * result comparison behavior. Don't forget to assert something! In case of test failures, use {@link #getResultType()}
	 * to tell the tester how to update the result file. 
	 * 
	 * @param actual
	 *            the processed model
	 * @param expected
	 *            the stored result
	 */
	public abstract void compareResults(R actual, R expected);

}
