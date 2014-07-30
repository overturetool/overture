package org.overture.core.tests;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Test with fine grained parsing and type checking. This class is very similar to {@link ParamStandardTest} but it is
 * capable of handling VDM sources that do not parse or type check correctly. <br>
 * <br>
 * The working principles behind this class are very similar to ParamStandardTest but you are responsible for calling
 * the parser and type checker yourself.
 * 
 * @author ldc
 * @param <R>
 *            the type of result this test operates on
 */
@RunWith(Parameterized.class)
abstract public class ParamFineGrainTest<R> extends
		AbsResultTest<R>
{

	protected String modelPath;

	/**
	 * Constructor simply passes parameters up to super class.
	 * 
	 * @param nameParameter
	 *            the name of the test. Usually derived from the VDM source
	 * @param testParameter
	 *            the path to the VDM source this test will run on
	 * @param resultParameter
	 *            the path to the stored result file
	 */
	public ParamFineGrainTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		this.testName = nameParameter;
		this.modelPath = testParameter;
		this.resultPath = resultParameter;
		this.updateResult = updateCheck();
	}

	/**
	 * The main test executor. Takes a VDM source (or any other text file) and processes it according to
	 * {@link #processSource(String)}. It then reads a saved result via {@link #deSerializeResult(String)}. Finally, the
	 * two results are compared via {@link #compareResults(R, R)}. <br>
	 * <br>
	 * If the test is running in update mode, then no comparison will be made and the new result will be saved instead.
	 * 
	 * @throws IOException
	 * @throws LexException
	 * @throws ParserException
	 */
	@Test
	public void testCase() throws ParserException, LexException, IOException
	{
		R actual = processSource();
		if (updateResult)
		{
			this.testUpdate(actual);
		} else
		{
			R expected=null;
			try {
				expected= deSerializeResult(resultPath);
			}
			catch (FileNotFoundException e){
				Assert.fail("Test " + testName
						+ " failed. No result file found. Use \"-D"
						+ getUpdatePropertyString() + "." + testName
						+ "\" to create an initial one."
						+ "\n The test result was: "+ actual.toString());
			}
			this.compareResults(actual, expected);
		}
	}

	/**
	 * Process a VDM source (or any other text file) and do whatever. This method is called on the test input as part of
	 * the main test execution. It must return a result of type <code>R</code>.
	 * 
	 * @return a test result of type <code>R</code>
	 */
	abstract public R processSource();

}
