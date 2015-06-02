package org.overturetool.cgisa;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.core.tests.ParseTcFacade;

@RunWith(Parameterized.class)
public class IsaGenExpTest

{

	String expression;
	String expected;

	public IsaGenExpTest(String input, String result)
	{
		this.expression = input;
		this.expected = result;
	}

	@Parameters(name = "{index}: exp: {0}")
	public static Collection<Object[]> getParams()
	{
		Object[] a1 = { "1+1", "(1 + 1)" };
		Object[] a2 = { "1-1", "(1 - 1)" };
		Object[] a3 = { "1/1", "1/1" }; //FIXME talk to PVJ about real division
		Object[] a4 = { "1*1", "(1 * 1)" };

		return Arrays.asList(new Object[][] { a1,a2,a4 });

	}

	@Test
	public void testCase() throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		PExp exp = ParseTcFacade.parseTcExpressionString(expression);
		IsaGen isagen = new IsaGen();
		GeneratedModule actual = isagen.generateIsabelleSyntax(exp);
		
		if (actual.hasMergeErrors()){
			fail(actual.getMergeErrors().toString());
		}
		
		if (actual.hasUnsupportedTargLangNodes()){
			fail(actual.getUnsupportedInTargLang().toString());
		}
		
		assertEquals(expected, actual.getContent());

	}

}
