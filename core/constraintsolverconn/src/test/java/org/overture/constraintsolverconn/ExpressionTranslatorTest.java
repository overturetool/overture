package org.overture.constraintsolverconn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.constraintsolverconn.visitor.CscVisitor;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

@RunWith(value = Parameterized.class)
public class ExpressionTranslatorTest
{

	@Parameters(name = "{index}: \"{0}\"")
	public static Collection<Object[]> getData()
	{
		List<Object[]> data = new Vector<Object[]>();

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/Sample.vpp".replace('/', '\\')), "UTF-8"));
			String line;

			while ((line = br.readLine()) != null)
			{
				line = line.trim();
				if (!line.substring(0, 2).equals("--"))
				{
					data.add(new Object[] { line });
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

	public ExpressionTranslatorTest(String exp)
	{
		this.expression = exp;
	}

	@Test
	public void test() throws IOException
	{
		PExp exp = null;

		try
		{
			exp = parse(expression);
		} catch (AnalysisException e)
		{
			Assert.fail("Invalid expression: " + expression);
		}

		try
		{
			CscVisitor visitor = new CscVisitor();

			String bExpression = exp.apply(visitor, "Some information #0 in Csc.java");
			System.out.println("Translated \"" + expression + "\" into \""
					+ bExpression);
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			Assert.fail("Internal error");
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
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		return typeCheckResult.result;
	}
}
