//package org.overture.ast.tests;
//
//import junit.framework.TestCase;
//
//import org.overture.ast.expressions.ABinopExp;
//import org.overture.ast.expressions.AIntConstExp;
//import org.overture.ast.expressions.APlusBinop;
//import org.overture.ast.expressions.PExp;
//import org.overture.ast.node.tokens.TNumbersLiteral;
//import org.overture.interpreter.ast.expressions.ABinopExpInterpreter;
//import org.overture.interpreter.ast.expressions.AIntConstExpInterpreter;
//import org.overture.interpreter.ast.expressions.APlusBinopInterpreter;
//import org.overture.interpreter.ast.node.CopyAdaptor;
//import org.overturetool.vdmj.lex.LexLocation;
//
//public class TestAstCopy extends TestCase
//{
//	public void testCopyBinop()
//	{
//		PExp exp = new ABinopExp(new AIntConstExp(new TNumbersLiteral("2")), new APlusBinop(new LexLocation()), new AIntConstExp(new TNumbersLiteral("5")));
//
//		CopyAdaptor adpator = new CopyAdaptor();
//
//		ABinopExpInterpreter res = (ABinopExpInterpreter) exp.apply(adpator);
//
//		assertNotNull(res.getLeft());
//		assertTrue(res.getLeft() instanceof AIntConstExpInterpreter);
//		assertTrue(((AIntConstExpInterpreter) res.getLeft()).getTNumbersLiteralInterpreter().getText().endsWith("2"));
//		
//		assertTrue(res.getPBinopInterpreter() instanceof APlusBinopInterpreter);
//		
//		assertNotNull(res.getRight());
//		assertTrue(res.getRight() instanceof AIntConstExpInterpreter);
//		assertTrue(((AIntConstExpInterpreter) res.getRight()).getTNumbersLiteralInterpreter().getText().endsWith("5"));
//	}
//}
