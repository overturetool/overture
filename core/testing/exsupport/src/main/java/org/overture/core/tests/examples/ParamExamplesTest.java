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
package org.overture.core.tests.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overture.ast.node.INode;
import org.overture.core.tests.AbsResultTest;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Test on the Overture examples. The behavior of class is very similar to that
 * of {@link ParamStandardTest}. The only difference is that the test inputs are
 * not user-configurable. They are provided directly by this class and consist
 * of the standard Overture examples.<br>
 * <br>
 * It is recommended that all analysis modules implement a version of this test
 * to ensure that they work on the provided examples.
 * 
 * @author ldc
 * @param <R>
 */
@RunWith(JUnitParamsRunner.class)
public abstract class ParamExamplesTest<R> extends AbsResultTest<R> {
	List<INode> model;

	private final static String RESULTS_EXAMPLES = "src/test/resources/examples/";

	/**
	 * Provide test data. Provides an array of {@link ExampleSourceData}, each
	 * holding data (including sources) of an Overture example.
	 * 
	 * Each entry initializes a test for a single Overture example. Test results
	 * are derived from the names of the examples and ,by convention, are stored
	 * under the <code>src/test/resources/examples</code> folder of each module
	 * using this test.
	 * 
	 * @return a collection of {@link ExampleSourceData}
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Object[] testData() throws ParserException, LexException,
			IOException, URISyntaxException {
		return ExamplesUtility.getExamplesAsts(getRelativeExamplesPath())
				.toArray();
	}

	/**
	 * Execute this test. Takes the model sources, parses, type checks them then
	 * takes the AST and applies whatever analysis is implemented in
	 * {@link #processModel(List)}. Afterwards, results are compared with
	 * {@link #compareResults(Object, Object)}. <br>
	 * <br>
	 * If the test is running in update mode, testUpdate(Object) is executed
	 * instead of the comparison.
	 * 
	 * @param exSource
	 *            holding the example data. Provided by {@link #testData()}
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserException
	 * @throws LexException
	 */
	@Test
	@Parameters(method = "testData")
	public void testCase(ExampleSourceData exSource)
			throws FileNotFoundException, IOException, ParserException,
			LexException {

		List<String> toSkip = getExamplesToSkip();
		Assume.assumeFalse(toSkip.contains(exSource.getName()));
		
		ExampleAstData exData = ExamplesUtility.parseTcExample(exSource);

		this.testName = exData.getExampleName();
		this.model = exData.getModel();
		this.resultPath = RESULTS_EXAMPLES + exData.getExampleName()
				+ PathsProvider.RESULT_EXTENSION;
		this.updateResult = updateCheck();

		R actual = processModel(model);
		if (updateResult) {
			testUpdate(actual);
		} else {
			R expected = null;
			try {
				expected = deSerializeResult(resultPath);
			} catch (FileNotFoundException e) {
				Assert.fail("Test " + testName
						+ " failed. No result file found. Use \"-D"
						+ getUpdatePropertyString() + "." + testName
						+ "\" to create an initial one."
						+ "\n The test result was: " + actual.toString());
			}
			this.compareResults(actual, expected);
		}
	}

	/**
	 * Analyse a model. This method is called during test execution to produce
	 * the actual result. It must, of course, be overridden to perform whatever
	 * analysis the functionality under test performs.<br>
	 * <br>
	 * The output of this method must be of type <code>R</code>, the result type
	 * this test runs on. You will will likely need to have a conversion method
	 * between the output of your analysis and <code>R</code>.
	 * 
	 * @param model
	 *            the model to process
	 * @return the output of the analysis
	 */
	public abstract R processModel(List<INode> model);

	/**
	 * Get the path to the examples. Not all modules that use the test framework
	 * are at the same nesting level from <code>core</code> so you must provide
	 * the path via this method. <br>
	 * <br>
	 * For reference the examples are in
	 * <code>[repository root]/externals/examples/target/code>
	 * 
	 * @return the path to the Overture examples, relative to the current
	 *         project.
	 */
	protected abstract String getRelativeExamplesPath();

	/**
	 * Get the examples to skip. This method returns an empty list by default and should be overridden in order to skip
	 * specific examples when running the test.
	 * 
	 * @return a {@link List} of {@link String} with the names of the examples to be skipped
	 */
	protected List<String> getExamplesToSkip()
	{
		return new LinkedList<String>();
	}
}
