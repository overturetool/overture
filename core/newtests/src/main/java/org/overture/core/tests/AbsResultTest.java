package org.overture.core.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Top level class for new tests framework. Provides common result handling code to all other test classes. This class
 * should<b>not</b> be subclass. Use {@link ParamStandardTest}, {@link ParamExamplesTest} or {@link ParamExternalsTest}
 * instead.
 * 
 * @author ldc
 * @param <R>
 *            the (user-provided) type of results this test operates on
 */
abstract class AbsResultTest<R extends Serializable>
{
	protected boolean updateResult;
	protected String resultPath;
	protected String testName;

	/**
	 * This method tries its best to deserialize any results file. If your results are too complex for it to handle, you
	 * should override {@link #getResultType()} to deal with it. If that fails, override this entire.
	 * 
	 * @param resultPath
	 * @return the stored result
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
			f.createNewFile();
			Assert.fail("Test " + testName
					+ " failed. No result file found. Use "
					+ getUpdatePropertyString() + "." + testName
					+ "to create an initial one.");
		}
		String json = IOUtils.toString(new FileReader(resultPath));
		R result = gson.fromJson(json, resultType);
		return result;
	}

	/**
	 * Calculates the type of the test result. This method does its best but will most likely need to be overridden.
	 * However, that's quite simply and you can use the same code as below but replacing the type parameter <code>R</code>
	 * with the actual explicit type of your result.
	 * 
	 * @return the {@link Type} of the result file
	 */
	public Type getResultType()
	{
		Type resultType = new TypeToken<R>()
		{
		}.getType();
		return resultType;
	}

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
	 * Update the result file for this test.
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
		IOUtils.write(json, new FileOutputStream(resultPath));
	}

	/**
	 * This method check if the test is being run in result update mode, by consulting the update property as returned
	 * by {{@link #getUpdatePropertyString()}.
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
	 * Compares output of the processed model with a previously stored result. This method must be overridden to
	 * implement result comparison behavior. Don't forget to assert something.
	 * 
	 * @param actual
	 *            the processed model
	 * @param expected
	 *            the stored result
	 */
	public abstract void compareResults(R actual, R expected);

}
