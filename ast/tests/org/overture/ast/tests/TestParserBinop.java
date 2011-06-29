package org.overture.ast.tests;

import org.overture.ast.expressions.ABinopExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.APlusBinop;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.tokens.TNumbersLiteral;
import org.overturetool.vdmj.lex.LexLocation;

import junit.framework.TestCase;

public class TestParserBinop extends TestCase
{
	public void testBinopPlus()
	{
		PExp exp = new ABinopExp(new AIntConstExp(new TNumbersLiteral("2")), new APlusBinop(new LexLocation()), new AIntConstExp(new TNumbersLiteral("5")));
	}
}
