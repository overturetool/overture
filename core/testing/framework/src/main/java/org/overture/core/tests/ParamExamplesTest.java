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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.node.INode;
import org.overture.core.tests.examples.ExampleAstData;
import org.overture.core.tests.examples.ExamplesUtility;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Test on the Overture examples. The behavior of class is very similar to that of {@link ParamStandardTest}. The only
 * difference is that the test inputs are not user-configurable. They are provided directly by this class and consist of
 * the standard Overture examples.<br>
 * <br>
 * It is recommended that all plug-ins implement a version of this test to ensure that they work on the provided
 * examples.
 * 
 * @author ldc
 * @param <R>
 */
@RunWith(Parameterized.class)
public abstract class ParamExamplesTest<R> extends AbsResultTest<R>
{
	List<INode> model;

	private final static String RESULTS_EXAMPLES = "src/test/resources/examples/";

	/**
	 * The constructor for the class. The parameters for the constructor are provided by {@link #testData()}. Due to
	 * this, subclasses of this test must have the exact same constructor parameters. If you change the constructor
	 * parameters, you must implement your own test data provider.
	 * 
	 * @param name
	 *            the name of the test. Normally derived from the example used as input
	 * @param model
	 *            the typed AST representing the example model under test
	 * @param result
	 *            the result file path. By convention it's stored under <code>src/test/resources/examples</code>
	 */
	public ParamExamplesTest(String name, List<INode> model, String result)
	{
		this.testName = name;
		this.model = model;
		this.resultPath = result;
		this.updateResult = updateCheck();
	}

	/**
	 * Provide test data. Provides a list of of arrays to initialize the test constructor. Each array initializes a test
	 * for a single Overture example. The arrays consist of a test name (derived from the example name), the AST for
	 * that example and a path to the result file. By convention, results are stored under the
	 * <code>src/test/resources/examples</code> folder of each module using this test.
	 * 
	 * @return a collection of model ASTs and result paths in the form of {modelname ,modelast, resultpath} arrays
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException,
			LexException, IOException, URISyntaxException
	{
		Collection<ExampleAstData> examples = ExamplesUtility.getExamplesAsts();
		Collection<Object[]> r = new Vector<Object[]>();

		for (ExampleAstData e : examples)
		{
			r.add(new Object[] {
					e.getExampleName(),
					e.getModel(),
					RESULTS_EXAMPLES + e.getExampleName()
							+ PathsProvider.RESULT_EXTENSION });
		}

		return r;
	}

	/**
	 * Execute this test. Takes the model AST and applies whatever analysis is implemented in
	 * {@link #processModel(List)}. Afterwards, results are compared with {@link #compareResults(Object, Object)}. <br>
	 * <br>
	 * If the test is running in update mode, testUpdate(Object) is executed instead of the comparison.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserException
	 * @throws LexException
	 */
	@Test
	public void testCase() throws FileNotFoundException, IOException,
			ParserException, LexException
	{

		R actual = processModel(model);
		if (updateResult)
		{
			testUpdate(actual);
		} else
		{
			R expected = null;
			try
			{
				expected = deSerializeResult(resultPath);
			} catch (FileNotFoundException e)
			{
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
	 * Analyse a model. This method is called during test execution to produce the actual result. It must, of course, be
	 * overridden to perform whatever analysis the functionality under test performs.<br>
	 * <br>
	 * The output of this method must be of type <code>R</code>, the result type this test runs on. You will will likely
	 * need to have a conversion method between the output of your analysis and <code>R</code>.
	 * 
	 * @param model
	 *            the model to process
	 * @return the output of the analysis
	 */
	public abstract R processModel(List<INode> model);

}
