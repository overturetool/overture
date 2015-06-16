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

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BinaryExpTest extends AbsExpTest

{

	String expression;
	String expected;

	public BinaryExpTest(String input, String result)
	{
		super(input, result);
	}

	@Parameters(name = "{index}: exp: {0}")
	public static Collection<Object[]> getParams()
	{
		Object[] a1 = { "1+1", "(1 + 1)" };
		Object[] a2 = { "1-1", "(1 - 1)" };
		Object[] a3 = { "1*1", "(1 * 1)" };
		Object[] a4 = { "1/1", "(1 / 1)" }; 
		
		Object[] a5 = { "1>1", "(1 > 1)" };
		Object[] a6 = { "1<>1", "(1 <> 1)" };
		
		return Arrays.asList(new Object[][] { a1, a2, a3,a4,a5,a6});

	}

}
