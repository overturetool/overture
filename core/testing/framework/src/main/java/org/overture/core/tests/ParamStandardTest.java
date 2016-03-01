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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Standard test in the new Overture test framework. This class runs tests on (correct) VDM sources and compares them
 * with stored result files. It is meant to be subclassed as a way to quickly create your own test suites.<br>
 * <br>
 * A comparison method for results must be provided. JSON-based serialization of results is fully automated and
 * deserialization is close to it.<br>
 * <br>
 * These tests are meant to be run as parameterized JUnit test and so any subclass must be annotated with
 * <code>@RunWith(Parameterized.class)</code>. <br>
 * <br>
 * This class also has a type parameter <code>R</code> that represents the output of the functionality under test. You
 * should create a specific <code>R</code> type for your functionality and write some kind of transformation between
 * your native output type and <code>R</code>.
 * 
 * @author ldc
 * @param <R> 
 */
public abstract class ParamStandardTest<R> extends AbsResultTest<R>
{

	protected String modelPath;

	/**
	 * Constructor for the test. In order to use JUnit parameterized tests, the inputs for this class must be supplied
	 * by a public static method. Subclasses must implement this method themselves and annotate with
	 * <code>@Parameters(name = "{index} : {0}")</code>.<br>
	 * <br>
	 * The {@link PathsProvider#computePaths(String...)} method produces the correct input for this constructor and
	 * should be called with the root folder of your tests inputs as its argument.
	 * 
	 * @param nameParameter
	 *            the name of the test. Normally derived from the test input file
	 * @param inputParameter
	 *            file path for the VDM source to test
	 * @param resultParameter
	 *            test result file
	 */
	public ParamStandardTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		this.testName = nameParameter;
		this.modelPath = inputParameter;
		this.resultPath = resultParameter;
		updateResult = updateCheck();
	}

	/**
	 * Execute this test. Constructs the AST for the model and processes it via {@link #processModel(List)}. Then loads
	 * a stored result via {@link #deSerializeResult(String)}. Finally, the two results are compared with
	 * {@link #compareResults(Object, Object)}.<br>
	 * <br>
	 * If the test is run in update mode, then no comparison is made. Instead, the new result is saved. <br>
	 * <br>
	 * This test is not designed to run on VDM sources with syntax or type errors. The test will fail if the source
	 * fails to parse or type check. While this behavior can be overridden, we suggest looking at
	 * {@link ParamFineGrainTest} if you need to cope with these errors.
	 * 
	 * @throws IOException
	 * @throws LexException
	 * @throws ParserException
	 */
	@Test
	public void testCase() throws ParserException, LexException, IOException
	{

		checkAssumptions();
		List<INode> ast = ParseTcFacade.typedAst(modelPath, testName);
		R actual = processModel(ast);
		if (updateResult)
		{
			this.testUpdate(actual);
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
	 * @param ast
	 *            the model to process
	 * @return the output of the analysis
	 */
	public abstract R processModel(List<INode> ast);

	protected void checkAssumptions() {}
}
