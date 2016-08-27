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
package org.overture.core.testing;

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
abstract public class ParamFineGrainTest<R> extends AbsResultTest<R>
{

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
	 * {@link #processSource()}. It then reads a saved result via {@link #deSerializeResult(String)}. Finally, the
	 * two results are compared via {@link #compareResults(Object, Object)}. <br>
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
	 * Process a VDM source (or any other text file) and do whatever. This method is called on the test input as part of
	 * the main test execution. It must return a result of type <code>R</code>.
	 * 
	 * @return a test result of type <code>R</code>
	 */
	abstract public R processSource();

}
