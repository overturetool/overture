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
	public void testCaseARemBinaryExp_O3() throws AnalysisException
	{
		aux("x rem 1", "(x rem 1)");
	}
	
	@Test
	public void testCaseARemBinaryExp_01() throws AnalysisException
	{
		aux("1 rem 1", "(1 rem 1)");
	}
	
	@Test
	public void testCaseARemBinaryExp_02() throws AnalysisException
	{
		aux("1.1 rem 1", "(1.1 rem 1)");
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
	public void testCaseABooleanImpliesExp_04() throws AnalysisException
	{
		aux("true => false","(true => false)");
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
	public void testCaseAAndBooleanBinaryExp_05() throws AnalysisException
	{
		aux("true and y", "(true and y)");
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
	public void testCaseAEqualsBinaryExp_05() throws AnalysisException
	{
		aux("{1} = {3}", "({1} = {3})");
	}
	
	@Test
	public void testCaseAEqualsBinaryExp_06() throws AnalysisException
	{
		aux("true = true", "(true = true)");
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
	
	@Test
	public void testCaseALessBinaryExp_01() throws AnalysisException
	{
		aux("0 < 1", "(0 < 1)");
	}
	
	@Test
	public void testCaseALessBinaryExp_02() throws AnalysisException
	{
		aux("x - 1 < 3","((x - 1) < 3)");
	}
	
	@Test
	public void testCaseALessBinaryExp_03() throws AnalysisException
	{
		aux("(x * 1) < 1","((x * 1) < 1)");
	}
	
	@Test
	public void testCaseAGreaterEqualBinaryExp_01() throws AnalysisException
	{
		aux("0 >= 1", "(0 >= 1)");
	}
	
	@Test
	public void testCaseAGreaterEqualBinaryExp_02() throws AnalysisException
	{
		aux("x - 1 >= 3","((x - 1) >= 3)");
	}
	
	@Test
	public void testCaseAGreaterEqualBinaryExp_03() throws AnalysisException
	{
		aux("(x * 1) >= 1","((x * 1) >= 1)");
	}
	
	@Test
	public void testCaseALessqualBinaryExp_01() throws AnalysisException
	{
		aux("0 <= 1", "(0 <= 1)");
	}
	
	@Test
	public void testCaseALessEqualBinaryExp_02() throws AnalysisException
	{
		aux("x - 1 <= 3","((x - 1) <= 3)");
	}
	
	@Test
	public void testCaseALessEqualBinaryExp_03() throws AnalysisException
	{
		aux("(x * 1) <= 1","((x * 1) <= 1)");
	}
	
	@Test
	public void testCaseACompBinaryExp_01() throws AnalysisException
	{
		aux("m1 comp m2", "(m1 comp m2)");
	}
	
	public void testCaseANotEqualBinaryExp_01() throws AnalysisException
	{
		aux("0 <> 1", "(0 <> 1)");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_02() throws AnalysisException
	{
		aux("x - 1 <> 3","((x - 1) <> 3)");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_03() throws AnalysisException
	{
		aux("(x * 1) <> 1","((x * 1) <> 1)");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_04() throws AnalysisException
	{
		aux("true <> false", "(true <> false)");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_05() throws AnalysisException
	{
		aux("'a' <> 'b'", "('a' <> 'b')");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_06() throws AnalysisException
	{
		aux("{2} <> {3}", "({2} <> {3})");
	}
	
	@Test
	public void testCaseANotEqualBinaryExp_07() throws AnalysisException
	{
		aux("<a> <> <b>", "(<a> <> <b>)");
	}
	
//	@Test
//	public void testCaseAMapletExp_02() throws AnalysisException
//	{
//		aux("{1 |-> 2}","({1 |-> 2})");
//	}
	
	@Test
	public void testCaseACardUnaryExp_01() throws AnalysisException
	{
		aux("card m", "(card m)");
	}
	
	@Test
	public void testCaseACardUnaryExp_02() throws AnalysisException
	{
		aux("card {1,2,3}", "(card {1, 2, 3})");
	}
	
	@Test
	public void testCaseAPowerSetUnaryExp01() throws AnalysisException
	{
		aux("power m", "(power m)");
	}
	@Test
	public void testCaseAPowerSetUnaryExp_02() throws AnalysisException
	{
		aux("power {1,2,3}", "(power {1, 2, 3})");
	}
	
	@Test
	public void testCaseAInSetBinaryExp_01() throws AnalysisException
	{
		aux("1 in set y", "(1 in set y)");
	}
	
	@Test
	public void testCaseAInSetBinaryExp_02() throws AnalysisException
	{
		aux("x in set y", "(x in set y)");
	}
	
	@Test
	public void testCaseAInSetBinaryExp_03() throws AnalysisException
	{
		aux("{1} in set {1, 2, 3}", "({1} in set {1, 2, 3})");
	}
	
	@Test
	public void testCaseANotInSetBinaryExp_01() throws AnalysisException
	{
		aux("1 not in set y", "(1 not in set y)");
	}
	
	@Test
	public void testCaseANotInSetBinaryExp_02() throws AnalysisException
	{
		aux("x not in set y", "(x not in set y)");
	}
	
	@Test
	public void testCaseANotInSetBinaryExp_03() throws AnalysisException
	{
		aux("{1} not in set {1, 2, 3}", "({1} not in set {1, 2, 3})");
	}
	
	@Test
	public void testCaseAUnionBinaryExp_01() throws AnalysisException
	{
		aux("1 union y", "(1 union y)");
	}
	
	@Test
	public void testCaseAUnionBinaryExp_02() throws AnalysisException
	{
		aux("x union y", "(x union y)");
	}
	
	@Test
	public void testCaseAUnionBinaryExp_03() throws AnalysisException
	{
		aux("{1} union {1, 2, 3}", "({1} union {1, 2, 3})");
	}
	
	@Test
	public void testCaseAIntersectBinaryExp_01() throws AnalysisException
	{
		aux("1 union y", "(1 union y)");
	}
	
	@Test
	public void testCaseAIntersectBinaryExp_02() throws AnalysisException
	{
		aux("x union y", "(x union y)");
	}
	
	@Test
	public void testCaseAIntersectBinaryExp_03() throws AnalysisException
	{
		aux("{1} union {1, 2, 3}", "({1} union {1, 2, 3})");
	}
	
	@Test
	public void testCaseADiffBinaryExp_01() throws AnalysisException
	{
		aux("1 \\ y", "(1 \\ y)");
	}
	
	@Test
	public void testCaseADiffBinaryExp_02() throws AnalysisException
	{
		aux("x \\ y", "(x \\ y)");
	}
	
	@Test
	public void testCaseADiffBinaryExp_03() throws AnalysisException
	{
		aux("{1} subset {1, 2, 3}", "({1} subset {1, 2, 3})");
	}
	
	@Test
	public void testCaseASubSetBinaryExp_01() throws AnalysisException
	{
		aux("1 subset y", "(1 subset y)");
	}
	
	@Test
	public void testCaseASubSetBinaryExp_02() throws AnalysisException
	{
		aux("x subset y", "(x subset y)");
	}
	
	@Test
	public void testCaseASubSetBinaryExp_03() throws AnalysisException
	{
		aux("{1} subset {1, 2, 3}", "({1} subset {1, 2, 3})");
	}
	
	@Test
	public void testCaseAProperSubSetBinaryExp_01() throws AnalysisException
	{
		aux("1 psubset y", "(1 psubset y)");
	}
	
	@Test
	public void testCaseAProperSubSetBinaryExp_02() throws AnalysisException
	{
		aux("x psubset y", "(x psubset y)");
	}
	
	@Test
	public void testCaseAProperSubSetBinaryExp_03() throws AnalysisException
	{
		aux("{1} psubset {1, 2, 3}", "({1} psubset {1, 2, 3})");
	}
	
	@Test
	public void testCaseADUnionUnaryExp_01() throws AnalysisException
	{
		aux("dunion {{1},{2}}", "(dunion {{1}, {2}})");
	}
	
	@Test
	public void testCaseADUnionUnaryExp_02()throws AnalysisException
	{
		aux("dunion {{x},{2}}", "(dunion {{x}, {2}})");
	}
	
	@Test
	public void testCaseADUnionUnaryExp_03() throws AnalysisException
	{
		aux("dunion {{},{2}}", "(dunion {{}, {2}})");
	}
	
	@Test
	public void testCaseADintersectUnaryExp_01() throws AnalysisException
	{
		aux("dinter {{1},{2}}", "(dinter {{1}, {2}})");
	}
	@Test
	public void testCaseADintersectUnaryExp_02()throws AnalysisException
	{
		aux("dinter {{x},{2}}", "(dinter {{x}, {2}})");
	}
	
	@Test
	public void testCaseAintersectUnaryExp_03() throws AnalysisException
	{
		aux("dinter {{},{2}}", "(dinter {{}, {2}})");
	}
	
	@Test
	public void testCaseAExistsExp_01() throws AnalysisException
	{
		aux("exists x in set y & x = 1", "exists x in set y & (x = 1)");
	}
	
	@Test
	public void testCaseAExistsExp_02() throws AnalysisException
	{
		aux("exists 1 in set {1, 2} & x < 2 and x > 0", 
				"exists 1 in set {1, 2} & ((x < 2) and (x > 0))");
	}
	
	@Test
	public void testCaseAExistsExp_03()	throws AnalysisException
	{
		aux("exists 1 in set {1, 2}, x in set {2, 3} & x < 2 and x > 0",
				"exists 1 in set {1, 2}, x in set {2, 3} & ((x < 2) and (x > 0))");
	}
	
	@Test
	public void testCaseAExistsExp_04()	throws AnalysisException
	{
		aux("exists 1 in set {1, 2}, x in set {2, 3}, x in set {3, 4} & x < 2 and x > 0",
				"exists 1 in set {1, 2}, x in set {2, 3}, x in set {3, 4} & ((x < 2) and (x > 0))");
	}
	
	@Test
	public void testCaseAExists1Exp_01() throws AnalysisException
	{
		aux("exists1 x in set y & x = 1", "exists1 x in set y & (x = 1)");
	}
	
	@Test
	public void testCaseAExists1Exp_02() throws AnalysisException
	{
		aux("exists1 1 in set {1, 2} & x < 2 and x > 0", 
				"exists1 1 in set {1, 2} & ((x < 2) and (x > 0))");
	}
	
	@Test
	public void testCaseAForAllExp_01() throws AnalysisException
	{
		aux("forall x in set {1, 2} & x > 1", "forall x in set {1, 2} & (x > 1)");
	}
	
	@Test
	public void testCaseAForAllExp_02() throws AnalysisException
	{
		aux("forall 1 in set {1, 2}, x in set {2, 3}, x in set {3, 4} & x < 2 and x > 0",
				"forall 1 in set {1, 2}, x in set {2, 3}, x in set {3, 4} & ((x < 2) and (x > 0))");
	}
	
	@Test
	public void testCaseAForAllExp_03() throws AnalysisException
	{
		aux("forall x in set y & x = 1", "forall x in set y & (x = 1)");
	}
	
	@Test
	public void testCaseAIotaExp_01() throws AnalysisException
	{
		aux("iota x in set {1, 2} & x = 1", "iota x in set {1, 2} & (x = 1)");
	}
	
	@Test
	public void testCaseAIotaExp_02() throws AnalysisException
	{
		aux("iota x in set y & x = 1", "iota x in set y & (x = 1)");
	}
	
	@Test
	public void testCaseAIotaExp_03() throws AnalysisException
	{
		aux("iota 1 in set y & x = 1", "iota 1 in set y & (x = 1)");
	}
	
	@Test
	public void testCaseAIotaExp_04() throws AnalysisException
	{
		aux("iota 1 in set {1, 2} & x = 1", "iota 1 in set {1, 2} & (x = 1)");
	}
	
	@Test
	public void testCaseACasesExp_01() throws AnalysisException
	{
		aux("cases x: \n  1 -> x,\n  2 -> 2*x \nend", "cases x: \n  1 -> x,\n  2 -> (2 * x)\nend");
	}
	
	@Test
	public void testCaseACasesExp_02() throws AnalysisException
	{
		aux("cases x: \n 1 -> x,\n 2 -> 2*x, others -> 3*x \nend", 
			"cases x: \n  1 -> x,\n  2 -> (2 * x),\n  others -> (3 * x)\nend");
	}
	
	@Test
	public void testCasesASetCompExp_01() throws AnalysisException
	{
		aux("{x|x in set {1, 2} & x = true}", "{x|x in set {1, 2} & (x = true)}");
	}
	
	@Test
	public void testCasesASetCompExp_02() throws AnalysisException
	{
		aux("{x|x in set {1, 2} & x > 1}", "{x|x in set {1, 2} & (x > 1)}");
	}
	
	@Test
	public void testCasesASetCompExp_03() throws AnalysisException
	{
		aux("{x|x in set y & x <> true}", "{x|x in set y & (x <> true)}");
	}
	
	@Test
	public void testCasesASetCompExp_04() throws AnalysisException
	{
		aux("{x|x in set y & x < 'z'}", "{x|x in set y & (x < 'z')}");
	}
	
	@Test
	public void testCasesAInSetExp_01() throws AnalysisException
	{
		aux("{1, ..., 4}", "{1, ..., 4}");
	}
	
	@Test
	public void testCasesASeqEnumExp_01() throws AnalysisException
	{
		aux("[1,2,3]","[1, 2, 3]");
	}
	
	@Test
	public void testCasesASeqEnumExp_02() throws AnalysisException
	{
		aux("[[1,2],[2]]", "[[1, 2], [2]]");
	}
	
	@Test
	public void testCasesASeqCompExp_01() throws AnalysisException
	{
		aux("[x| x in set {1, 2} & i < 3]", "[x |x in set {1, 2} & (i < 3)]");
	}
	
	@Test
	public void testCasesASeqCompExp_02() throws AnalysisException
	{
		aux("[x*x| x in set {1, 2} & x <> 1]","[(x * x) |x in set {1, 2} & (x <> 1)]");
	}
	
	@Test
	public void testCasesAHeadUnaryExp_01() throws AnalysisException
	{
		aux("hd l", "(hd l)");
	}
	
	@Test
	public void testCasesAHeadUnaryExp_02() throws AnalysisException
	{
		aux("hd [1, 2]", "(hd [1, 2])");
	}
	
	@Test
	public void testCasesAHeadUnaryExp_03() throws AnalysisException
	{
		aux("hd [x*x]", "(hd [(x * x)])");
	}
	
	@Test
	public void testCasesATailUnaryExp_01() throws AnalysisException
	{
		aux("tl l", "(tl l)");
	}
	
	@Test
	public void testCasesATailUnaryExp_02() throws AnalysisException
	{
		aux("tl [1, 2]", "(tl [1, 2])");
	}
	
	@Test
	public void testCasesATailUnaryExp_03() throws AnalysisException
	{
		aux("tl [x*x]", "(tl [(x * x)])");
	}
	
	@Test
	public void testCasesALenUnaryExp_01() throws AnalysisException
	{
		aux("len l", "(len l)");
	}
	
	@Test
	public void testCasesALenUnaryExp_02() throws AnalysisException
	{
		aux("len [1, 2]", "(len [1, 2])");
	}
	
	@Test
	public void testCasesAlenUnaryExp_03() throws AnalysisException
	{
		aux("len [x*x]", "(len [(x * x)])");
	}
}