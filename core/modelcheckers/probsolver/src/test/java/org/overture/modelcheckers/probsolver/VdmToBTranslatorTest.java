/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

@RunWith(value = Parameterized.class)
public class VdmToBTranslatorTest
{

	@Parameters(name = "{index}: \"{0}\"")
	public static Collection<Object[]> getData()
	{
		List<Object[]> data = new Vector<Object[]>();

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/single-line-expressions/evaluate/expressions".replace('/', '\\')), "UTF-8"));
			String line;
			int lineIndex = 0;

			while ((line = br.readLine()) != null)
			{
				lineIndex++;
				line = line.trim();
				if (!line.startsWith("--") && !line.startsWith("//"))
				{
					int end = line.indexOf("//");

					if (end < 0)
					{
						end = line.indexOf("--");
					}

					if (end > 0)
					{
						line = line.substring(0, end);
					}
					data.add(new Object[] { line,
							line.replace("(", " ").replace(")", " "), lineIndex });
				}
			}
			br.close();
		} catch (IOException e)
		{
			System.out.println("IOException: " + e);
		}

		return data;
	}

	private final String expression;
	private final int lineIndex;

	public VdmToBTranslatorTest(String exp, String name, int line)
	{
		this.expression = exp;
		this.lineIndex = line;
	}

	@Before
	public void setup()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@Test
	public void test() throws IOException, AnalysisException
	{
		// Assume.assumeTrue(false);// comment this when the tests parse
		PExp exp = null;

		try
		{
			exp = parse(expression);
		} catch (AnalysisException e)
		{
			System.err.println("Line: " + lineIndex
					+ " Remove this expression it is invalid: " + expression);
			return;
			// Assert.fail("Invalid expression: " + expression);
		}

		VdmToBConverter translator = new VdmToBConverter();
		try
		{
			exp.apply(translator);

			if (!translator.unsupportedConstructs.isEmpty())
			{
				Assert.fail("Missing implementation for: "
						+ translator.unsupportedConstructs);
			}
		} catch (Exception e)
		{
			Assert.fail("translation error");
		}

	}

	private PExp parse(String exp) throws AnalysisException
	{
		if (exp == null || exp.isEmpty())
		{
			throw new AnalysisException("No expression to generate from");
		}

		ParserResult<PExp> parseResult = null;

		try
		{
			parseResult = ParserUtil.parseExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to parse expression: " + exp
					+ ". Message: " + e.getMessage());
		}

		if (parseResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to parse expression: " + exp);
		}

		TypeCheckResult<PExp> typeCheckResult = null;
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp + ". Message: " + e.getMessage());
		}

		if (typeCheckResult.errors.size() > 0)
		{
			// throw new AnalysisException("Unable to type check expression: "
			// + exp);
		}

		return typeCheckResult.result;
	}
}
