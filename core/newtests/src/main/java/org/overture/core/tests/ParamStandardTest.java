package org.overture.core.tests;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.junit.Test;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * The Standard Parameterized Test is the main test class for the new tests. This class runs tests on VDM source models
 * and compares them with stored result files. It is meant to be subclassed as a way to quickly create your own test
 * suites.<br>
 * <br>
 * A comparison method for results must be provided. Serialization of results is as automated as possible but can be
 * overridden if your results classes are too complex for auto-deserialization. <br>
 * <br>
 * These tests are meant to be used as parameterized JUnit test and so any subclass {@link ParamStandardTest} must be
 * annotated with <code>@RunWith(Parameterized.class)</code>. <br>
 * <br>
 * This class also has a type parameter <code>R</code> that represents the output of the functionality under test. These
 * types must implement {@link Serializable} and you will most likely need to write some kind of transformation between
 * your native output type and <code>R</code>.
 * 
 * @author ldc
 * @param R
 *            the result type being compared by the test
 */
public abstract class ParamStandardTest<R extends Serializable> extends
		AbsResultTest<R>
{

	protected String modelPath;

	/**
	 * Constructor for the test. Works with outputs gotten from {@link PathsProvider} which must to supplied via a
	 * static method. Subclasses must implement this method.<br>
	 * <br>
	 * In order to use JUnit parameterized tests, you must annotate the data-supplying method with <b>
	 * <code>@Parameters(name = "{index} : {0}")</code></b>. Supporting parameterized tests is also the reason the
	 * method must be static.
	 * 
	 * @param nameParameter
	 *            the name of the test. Normally derived from the test input file
	 * @param testParameter
	 *            file path for the VDM source to test
	 * @param resultParameter
	 *            test result file
	 */
	public ParamStandardTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		this.testName = nameParameter;
		this.modelPath = testParameter;
		this.resultPath = resultParameter;
		updateResult = updateCheck();
	}

	/**
	 * Analyses a model (represented by its AST). This method must be overridden to perform whatever analysis the
	 * functionality under test performs.<br>
	 * <br>
	 * The output of this method must be of type <code>R</code>, the result type this test runs on.
	 * 
	 * @param ast
	 *            the model to process
	 * @return the output of the analysis
	 */
	public abstract R processModel(List<INode> ast);

	/**
	 * The main test executor. Construct the AST for model and processes it via {@link #processModel(List)}. Then loads
	 * a stored result via {@link #deSerializeResult(String)}. Finally, the two results are compared with
	 * {@link #compareResults(Object, IResult)}.<br>
	 * <br>
	 * If the test is run in update mode, then no comparison is made. Instead, the new result is saved.
	 * 
	 * @throws IOException
	 * @throws LexException
	 * @throws ParserException
	 */
	@Test
	public void testCase() throws ParserException, LexException, IOException
	{

		List<INode> ast = ParseTcFacade.typedAst(modelPath, testName);
		R actual = processModel(ast);
		if (updateResult)
		{
			this.testUpdate(actual);
		} else
		{
			R expected = deSerializeResult(resultPath);
			this.compareResults(actual, expected);
		}
	}

}
