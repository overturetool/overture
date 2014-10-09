package org.overture.pog.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.VariableSubVisitor;

/**
 * Test {@link VariableSubVisitor} case-by-case. Before test runs, a substituion [a/1] is loaded into the visitor
 * 
 * @author ldc
 */
public class VariableSubTest
{

	VariableSubVisitor es;
	Substitution sub;

	public void aux(String input, String expected) throws AnalysisException,
			IOException
	{

		ParserResult<PExp> expInput = ParserUtil.parseExpression(input);
		String cleaned_actual = expInput.result.apply(es, sub).toString().replaceAll("[\\(\\)]", "");
		String cleaned_expected = expected.replaceAll("[\\(\\)]", "");
		assertEquals(cleaned_expected, cleaned_actual);
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.release = Release.DEFAULT;
		Settings.dialect = Dialect.VDM_SL;

		es = new VariableSubVisitor();

		AIntLiteralExp lit = new AIntLiteralExp();
		lit.setValue(new LexIntegerToken(1, null));

		sub = new Substitution(new LexNameToken("", "a", null), lit);
	}

	@Test
	public void testCaseAAbsoluteUnaryExp_Sub() throws AnalysisException,
			IOException
	{
		aux("abs a", "(abs 1)");
	}

	@Test
	public void testCaseAAbsoluteUnaryExp_NoSub() throws AnalysisException,
			IOException
	{
		aux("abs 1", "(abs 1)");
	}

	@Test
	public void testCaseAAndBooleanBinaryExp_sub() throws AnalysisException,
			IOException
	{
		aux("a and b", "1 and b)");
	}

	@Test
	public void testCaseAAndBooleanBinaryExp_NoSub() throws AnalysisException,
			IOException
	{
		aux("b and c", "(b and c)");
	}

	@Test
	public void testCaseAFieldExp_Sub() throws AnalysisException, IOException
	{
		aux("a.x", "1.x");
	}

	@Test
	public void testCaseAFieldExp_NoSub() throws AnalysisException, IOException
	{
		aux("b.x", "b.x");
	}

	@Test
	public void testCaseAMapDomainUnaryExp_01() throws AnalysisException,
			IOException
	{
		aux("dom a", "dom 1");
	}

}
