package com.lausdahl.ast.test2;

import generated.node.ATrueBoolean;
import generated.node.PType;

import org.overture.ast.expressions.ABinopExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntConstExp;
import org.overture.ast.expressions.APlusBinop;
import org.overture.ast.expressions.PExp;
import org.overture.ast.tokens.TNumbersLiteral;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;

import com.lausdahl.ast.values.IValue;
import com.lausdahl.runtime.Context;

public class ExtendedTest
{

	public static void test()
	{
		System.out.println("====================== Extended TEST 1 =========================");
		PExp exp = null;// p.parse();

		exp = new ABinopExp(new AIntConstExp(new TNumbersLiteral("2")), new APlusBinop(new LexLocation()), new AIntConstExp(new TNumbersLiteral("5")));
		exp.apply(new PrettyPrint());

		check(exp);
		eval(exp);

		System.out.println();
		System.out.println("---------------------------------------------------------------");
		System.out.println();

		exp = new ABinopExp(new ABooleanConstExp(new ATrueBoolean()), new APlusBinop(new LexLocation()), new AIntConstExp(new TNumbersLiteral("5")));


		check(exp);
		eval(exp);

		System.out.println();
		System.out.println("---------------------------------------------------------------");
		System.out.println();

		exp.apply(new PrettyPrint());
	}

	public static void eval(PExp exp)
	{
		System.out.println();
		System.out.print("Evaluating: " + exp);
		IValue n = exp.apply(new EvaluationVisitor(), new Context());
		System.out.print(" = " + n);
		System.out.println();
	}

	public static void check(PExp exp)
	{
		System.out.println("Checking: " + exp);
		PType t = exp.apply(new TypeCheckVisitor(), new TypeCheckInfo());
		System.out.println("Type is: " + t);
	}

}
