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
 * should <b>not</b> be subclassed. Use one of its existing subclasses instead.
 * 
 * @see ParamExamplesTest
 * @see ParamExternalsTest
 * @see ParamFineGrainTest
 * @see ParamStandardTest
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
	 * Deserialize test results. This method is capable of deserializing most results, provided the correct type
	 * information is provided via {@link #getResultType()}. If your results are too complex for this method or if you
	 * are not using JSON to store then, them you must override the entire method.
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
			Assert.fail("Test " + testName
					+ " failed. No result file found. Use \"-D"
					+ getUpdatePropertyString() + "." + testName
					+ "\" to create an initial one.");
		}
		String json = IOUtils.toString(new FileReader(resultPath));
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
		IOUtils.write(json, new FileOutputStream(resultPath));
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
	 * Compare output of the processed model with previously stored result. This method must be overridden to
	 * implement result comparison behavior. Don't forget to assert something!
	 * 
	 * @param actual
	 *            the processed model
	 * @param expected
	 *            the stored result
	 */
	public abstract void compareResults(R actual, R expected);

}
