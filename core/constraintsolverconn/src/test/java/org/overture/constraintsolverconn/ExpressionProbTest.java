package org.overture.constraintsolverconn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Assume;
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
import org.overture.constraintsolverconn.visitor.CscVisitor;
import org.overture.constraintsolverconn.visitor.CscVisitor.UnsupportedConstruct;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

@RunWith(value = Parameterized.class)
public class ExpressionProbTest
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
				if (!line.startsWith("--") && !line.startsWith("//"))
				{
					int end = line.indexOf("//");
					
					if(end<0)
					{
						end =  line.indexOf("--");
					}
					
					if(end>0)
					{
						line = line.substring(0,end);
					}
					data.add(new Object[] { line,line.replace("(", "[|").replace(")","|]") });
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

	public ExpressionProbTest(String exp,String name)
	{
		this.expression = exp;
	}
	
	@Before
	public void setup()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
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

			String bExpression = exp.apply(visitor, null);
//			System.out.println("Translated \"" + expression + "\" into \""
//					+ bExpression+"\"");
//			System.out.println("Running prob...");
			//
			if(System.getenv("PROBCLI")==null)
			{
				Assume.assumeTrue("No prob cli avaliable", false);
				return;
			}
			
			File probcli = new File(System.getenv("PROBCLI"),"probcli");
			
			ProcessBuilder pb = new ProcessBuilder(probcli.getAbsolutePath(), "-p", "BOOL_AS_PREDICATE", "TRUE", "-p", "CLPFD", "TRUE", "-p", "MAXINT", "127", "-p", "MININT", "-128", "-p", "TIME_OUT", "500", "-eval", "\""
					+ bExpression + "\"");
			// System.out.println(pb.command());
//			pb.directory(new File("C:\\Users\\kel\\Downloads\\ProB.windows\\ProB"));
			pb.redirectErrorStream(true);
			Process p = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = input.readLine()) != null)
			{
				sb.append("\n" + line);
			}

			final String RESULT_TOKENS = "Evaluation results:";
			final String SOLUTION_TOKENS = "Solution:";

			String result = "";
			String solution="";

			if (sb.toString().contains(RESULT_TOKENS))
			{
				int resultIndex = sb.toString().indexOf(RESULT_TOKENS);
				result = sb.toString().substring(resultIndex
						+ RESULT_TOKENS.length());
				if (result.indexOf("\n") > 0)
				{
					result = result.substring(result.indexOf("\n")).trim();
				}
				result=result.trim();
				result = result.substring(1, result.length()-3);
				
				if (sb.toString().contains(SOLUTION_TOKENS))
				{
					int beginIndexSolution = sb.toString().indexOf(SOLUTION_TOKENS)
							+ SOLUTION_TOKENS.length();
					solution = sb.toString().substring(beginIndexSolution,resultIndex);
					
					solution = solution.replaceAll("\\s+", " ").replace('\n', '\t');
					
				}
			}

			
//			System.out.println("Got this from the solver: " + result +" Solution: "+solution);
			System.out.println(padRight("Solution: "+solution, 40)+padRight(" Result: "+ result,30)+ " Input: "+padRight(bExpression,40) + " Source: "+expression);

			if (result.contains("ERROR"))
			{
				Assert.fail("Syntax/Type reported by solver input: "
						+ bExpression);
			}

		} catch (UnsupportedConstruct e)
		{
			Assert.fail("Input contained unsupported construct: "+ expression +" at "+e.getMessage());
		} catch (AnalysisException e)

		{
			e.printStackTrace();
			Assert.fail("Internal error");
		}
	}
	
	public String padRight(String text, int count)
	{
		while(text.length()<count)
		{
			text+=" ";
		}
		return text;
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
