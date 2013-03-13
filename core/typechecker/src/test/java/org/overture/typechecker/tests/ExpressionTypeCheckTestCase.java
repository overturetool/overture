//package org.overture.typechecker.tests;
//
//import java.util.Vector;
//
//import org.overture.ast.definitions.PDefinition;
//import org.overture.ast.expressions.PExp;
//import org.overture.ast.types.PType;
//import org.overture.typecheck.Environment;
//import org.overture.typecheck.FlatCheckedEnvironment;
//import org.overture.typecheck.TypeCheckInfo;
//import org.overture.typecheck.TypeComparator;
//import org.overture.typecheck.visitors.TypeCheckVisitor;
//import org.overture.vdmj.lex.LexException;
//import org.overture.vdmj.syntax.ParserException;
//import org.overture.vdmj.typechecker.NameScope;
//
//public class ExpressionTypeCheckTestCase extends BasicTypeCheckTestCase
//{
//
//	public void test1() throws ParserException, LexException
//	{
//		expressionTc("2+3", "nat1");
//		// expressionTc("2+3.0", "nat1");
//		// expressionTc("if true then false else 5", "(bool|nat1)");
//		// expressionTc("hd []", "");
//		// expressionTc("dom {1|->2}", "");
//	}
//
//	public void test2() throws ParserException, LexException
//	{
//		// expressionTc("2+3", "nat1");
//		// expressionTc("2+3.0", "nat1");
//		expressionTc("if true then false else 5", "(bool)");
//		// expressionTc("hd []", "");
//		// expressionTc("dom {1|->2}", "");
//	}
//
//	private void expressionTc(String expressionString, String expectedTypeString)
//			throws ParserException, LexException
//	{
//		System.out.flush();
//		System.err.flush();
//
//		PExp exp = parse(ParserType.Expression, expressionString);
//		PType expectedType = getResultType(expectedTypeString);
//
//		System.out.println(exp.toString().replace('\n', ' ').replace('\r', ' '));
//
//		Environment env = new FlatCheckedEnvironment(new Vector<PDefinition>(), NameScope.NAMESANDSTATE);
//		TypeCheckVisitor tc = new TypeCheckVisitor();
//		PType type = exp.apply(tc, new TypeCheckInfo(env));
//
//		if (expectedType != null)
//		{
//			assertEquals("Type of \n"
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ " \nis: " + type + " \nexpected: " + expectedType + "\n", true, TypeComparator.compatible(expectedType, type));
//		}
//
//		if (type != null)
//		{
//			System.out.println("Type of \""
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ "\" is: " + type);
//		} else
//		{
//			System.err.println("Type of \""
//					+ exp.toString().replace('\n', ' ').replace('\r', ' ')
//					+ "\" is: " + type);
//		}
//		System.out.flush();
//		System.err.flush();
//	}
//
//}
