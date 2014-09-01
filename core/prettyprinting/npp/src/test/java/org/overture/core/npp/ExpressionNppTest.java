/*
 * #%~
 * New Pretty Printer
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
package org.overture.core.npp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.util.ParserUtil;

public class ExpressionNppTest {

	NewPrettyPrinter npp;

	/**
	 * Helper function. Parse a string, pretty print it and compare.
	 * 
	 * @param input
	 *            the string to parse
	 * @param expected
	 *            the pretty printed version of the string. Must match <b>
	 *            exactly</b>.
	 * @throws AnalysisException
	 */
	public void aux(String input, String expected)
			throws AnalysisException {
		PExp expInput = ParserUtil.parseExpression(input).result;
		String actual = NewPrettyPrinter.prettyPrint(expInput);
		assertEquals(expected, actual);
	}

	@Before
	public void setUp() throws Exception {
		npp = NewPrettyPrinter.newInstance();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	@Test
	public void testCaseAPlusBinaryExp_01() throws AnalysisException{
		aux("1+1","(1 + 1)");
	}
	
	@Test
	public void testCaseAPlusBinaryExp_02() throws AnalysisException{
		aux("1.1+1","(1.1 + 1)");
	}
	
	@Test
	public void testCaseAPlusBinaryExp_03() throws AnalysisException
	{
		aux("x+1", "(x + 1)");
	}

	@Test
	public void testCaseAPlusBinaryExp_04() throws AnalysisException{
		aux("1+1+1","((1 + 1) + 1)");
	}
	
	
	@Test
	public void testCaseAMinusBinaryExp_01() throws AnalysisException
	{
		aux("1-1", "(1 - 1)");
	}
	
	@Test
	public void testCaseAMinusBinaryExp_02() throws AnalysisException
	{
		aux("1.1-1", "(1.1 - 1)");
	}
	
	@Test
	public void testCaseAMinusBinaryExp_03() throws AnalysisException
	{
		aux("1-1-1", "((1 - 1) - 1)");
	}
	
	public void testCaseAMinusBinaryExp_04() throws AnalysisException
	{
		aux("x-1","(x - 1)");
	}
	
	@Test
	public void testCaseATimesBinaryExp_01() throws AnalysisException
	{
		aux("1*1", "(1 * 1)");
	}
	
	@Test
	public void testCaseATimesBinaryExp_02() throws AnalysisException
	{
		aux("1.1*1", "(1.1 * 1)");
	}
	
	@Test
	public void testCaseATimesBinaryExp_03() throws AnalysisException
	{
		aux("1*1*1","((1 * 1) * 1)");
	}
	
	@Test
	public void testCaseATimesBinaryExp_04() throws AnalysisException
	{
		aux("x*1", "(x * 1)");
	}
	
	@Test
	public void testCaseADivideBinaryExp_01() throws AnalysisException
	{
		aux("1/1","(1 / 1)");
	}
	
	@Test
	public void testCaseADivideBinaryExp_02() throws AnalysisException
	{
		aux("1.1/1","(1.1 / 1)");
	}
	
	@Test
	public void testCaseADivideBinaryExp_03() throws AnalysisException
	{
		aux("x/1","(x / 1)");
	}
	
	@Test
	public void testCaseAModBinaryExp_01() throws AnalysisException
	{
		aux("1 mod 1", "(1 mod 1)");
	}
	
	@Test
	public void testCaseAModBinaryExp_02() throws AnalysisException
	{
		aux("1.1 mod 1", "(1.1 mod 1)");
	}
	
	@Test
	public void tetestCaseAModBinaryExp_03() throws AnalysisException
	{
		aux("(1 mod 1) mod 1","((1 mod 1) mod 1)");
	}
	
	@Test
	public void testCaseAModBinaryExp_04() throws AnalysisException
	{
		aux("x mod 1", "(x mod 1)");
	}
	
	@Test
	public void testCaseADivBinaryExp_01() throws AnalysisException
	{
		aux("1 div 1", "(1 div 1)");
	}
	
	@Test
	public void testCaseADivBinaryExp_02() throws AnalysisException
	{
		aux("1.1 div 1", "(1.1 div 1)");
	}
	
	@Test
	public void testCaseABooleanImpliesExp_01() throws AnalysisException
	{
		aux("A => B", "(A => B)");
	}
	
	@Test
	public void testCaseABooleanImpliesExp_02() throws AnalysisException
	{
		aux("1 => 2","(1 => 2)");
	}
	
	
}
