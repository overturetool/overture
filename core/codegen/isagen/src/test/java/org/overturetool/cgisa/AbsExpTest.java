/*
 * #%~
 * The VDM to Isabelle Translator
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
