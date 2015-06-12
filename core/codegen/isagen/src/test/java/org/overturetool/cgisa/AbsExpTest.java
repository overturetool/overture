package org.overturetool.cgisa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.core.tests.ParseTcFacade;

@RunWith(Parameterized.class)
public abstract class AbsExpTest

{

	String expression;
	String expected;

	public AbsExpTest(String input, String result)
	{
		this.expression = input;
		this.expected = result;
	}

	@Test
	public void testCase() throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		PExp exp = ParseTcFacade.parseTcExpressionString(expression);
		IsaGen isagen = new IsaGen();
		GeneratedModule actual = isagen.generateIsabelleSyntax(exp);

		if (actual.hasMergeErrors())
		{
			fail(actual.getMergeErrors().toString());
		}

		if (actual.hasUnsupportedTargLangNodes())
		{
			fail(actual.getUnsupportedInTargLang().toString());
		}

		assertEquals(expected, actual.getContent());

	}

}
