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

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * No result file test in the new Overture test framework. This class runs tests on (correct) VDM sources. Test result
 * storage (if any) is left entirely up to the developer.
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
public abstract class NoResultFileTest<R>
{

	protected String modelPath;
	protected String testName;

	/**
	 * Constructor for the test. In order to use JUnit parameterized tests, the inputs for this class must be supplied
	 * by a public static method. Subclasses must implement this method themselves and annotate with
	 * <code>@Parameters(name = "{index} : {0}")</code>.<br>
	 * <br>
	 * The {@link PathsProvider#computePathsNoResultFiles(String...)} method produces the correct input for this constructor and
	 * should be called with the root folder of your tests inputs as its argument.
	 * 
	 * @param nameParameter
	 *            the name of the test. Normally derived from the test input file
	 * @param inputParameter
	 *            file path for the VDM source to test
	 */
	public NoResultFileTest(String nameParameter, String inputParameter)
	{
		this.testName = nameParameter;
		this.modelPath = inputParameter;
	}

	/**
	 * Execute this test. Constructs the AST for the model and processes it via {@link #processModel(List)}. Then the
	 * output of the analysis is evaluated via {@link #processResult(Object)}.
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

		List<INode> ast = ParseTcFacade.typedAst(modelPath, testName);
		R actual = processModel(ast);
		processResult(actual);
	}

	/**
	 * Analyse a test result. This method is called during test execution. It must, of course, be
	 * overridden to perform result analysis as desired for this test.Don't forget to assert something!
	 * 
	 * @param actual
	 *            the result of processing the model per {@link #processModel(List)}
	 */
	protected abstract void processResult(R actual);
	
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
}
