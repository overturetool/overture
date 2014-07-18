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
	public void testCaseADivBinaryExp_O3() throws AnalysisException
	{
		aux("x div 1", "(x div 1)");
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
	
	@Test
	public void testCaseAbooleanImpliesExp_03() throws AnalysisException
	{
		aux("x => 1","(x => 1)");
	}
	
	@Test
	public void testCaseAAbsoluteUnaryExp_01() throws AnalysisException
	{
		aux("abs 1","(abs 1)");
	}
	
	@Test
	public void testCaseAAbsoluteUnaryExp_02() throws AnalysisException
	{
		aux("abs 1.1","(abs 1.1)");
	}
	
	@Test
	public void testCaseAAbsoluteUnaryExp_03() throws AnalysisException
	{
		aux("abs x","(abs x)");
	}
	
	@Test
	public void testCaseAAndBooleanBinaryExp_01() throws AnalysisException
	{
		aux("A and B","(A and B)");
	}
	
	@Test
	public void testCaseAAndBooleanBinaryExp_02() throws AnalysisException
	{
		aux("1.1 and B","(1.1 and B)");
	}
	
	@Test
	public void testCaseΑAndBooleanBinaryExp_03() throws AnalysisException
	{
		aux("x and B","(x and B)");
	}
	
	@Test
	public void testCaseAAndBooleanBinaryExp_04() throws AnalysisException
	{
		aux("x and y", "(x and y)");
	}
	
	@Test
	public void testCaseAOrBooleanBinaryExp_01() throws AnalysisException
	{
		aux("A or B","(A or B)");
	}
	
	@Test
	public void testCaseAOrBooleanBinaryExp_02() throws AnalysisException
	{
		aux("1 or 1.1","(1 or 1.1)");
	}
	
	@Test
	public void testCaseAOrBooleanBinaryExp_03() throws AnalysisException
	{
		aux("x or 1","(x or 1)");
	}
	
	@Test
	public void testCaseAEqualsBinaryExp_01() throws AnalysisException
	{
		aux("1 = 1", "(1 = 1)");
	}
	
	@Test
	public void testCaseAEqualsBinaryExp_02() throws AnalysisException
	{
		aux("x = 1", "(x = 1)");
	}
	
	@Test
	public void testCaseAEqualsBinaryExp_03() throws AnalysisException
	{
		aux("x + 1 = 4","((x + 1) = 4)"); 
	}
	
	@Test
	public void testCaseAEqualsBinaryExp_04() throws AnalysisException
	{
		aux("1 + 2 - x = 6", "(((1 + 2) - x) = 6)");
	}
	
	@Test
	public void testCaseAGreaterBinaryExp_01() throws AnalysisException
	{
		aux("1 > 0", "(1 > 0)");
	}
	
	@Test
	public void testCaseAGreaterBinaryExp_02() throws AnalysisException
	{
		aux("x - 1 > 3","((x - 1) > 3)");
	}
	
	@Test
	public void testCaseAGreaterBinaryExp_03() throws AnalysisException
	{
		aux("(x * 1) > 1","((x * 1) > 1)");
	}
}