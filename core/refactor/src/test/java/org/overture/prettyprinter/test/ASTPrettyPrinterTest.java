package org.overture.prettyprinter.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.npp.NewPrettyPrinter;
import org.overture.parser.util.ParserUtil;

public class ASTPrettyPrinterTest {

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
