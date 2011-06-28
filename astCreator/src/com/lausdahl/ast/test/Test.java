//package com.lausdahl.ast.test;
//
//import generated.node.ABinopExp;
//import generated.node.ABooleanConstExp;
//import generated.node.AIntConstExp;
//import generated.node.ALazyAndBinop;
//import generated.node.APlusBinop;
//import generated.node.ATrueBoolean;
//import generated.node.Eval;
//import generated.node.PBinopEval;
//import generated.node.PBinopTypeChecker;
//import generated.node.PBooleanEval;
//import generated.node.PBooleanTypeChecker;
//import generated.node.PExp;
//import generated.node.PType;
//import generated.node.PTypeEval;
//import generated.node.PTypeTypeChecker;
//import generated.node.PUnopEval;
//import generated.node.PUnopTypeChecker;
//import generated.node.TAndAnd;
//import generated.node.TNumbersLiteral;
//import generated.node.TypeChecker;
//
//import org.overturetool.vdmj.lex.LexLocation;
//import org.overturetool.vdmj.lex.LexToken;
//import org.overturetool.vdmj.lex.VDMToken;
//
//import com.lausdahl.ast.values.IValue;
//import com.lausdahl.runtime.Environment;
//import com.lausdahl.runtime.NameScope;
//
//public class Test
//{
//
//	static TypeChecker typeChecker = new TypeChecker(new PBinopTypeChecker(), new PUnopTypeChecker(), new ExpressionTc(), new PBooleanTypeChecker(), new PTypeTypeChecker());
//
//	public static void run()
//	{
//		System.out.println("============================ TEST 1 ===========================");
//		PExp exp = null;// p.parse();
//
//		exp = new ABinopExp(new AIntConstExp(new TNumbersLiteral("2")), new APlusBinop(new LexToken(new LexLocation(),VDMToken.PLUS)), new AIntConstExp(new TNumbersLiteral("5")));
//
//		if (check(exp))
//		{
//			eval(exp);
//		}
//
//		System.out.println();
//		System.out.println("---------------------------------------------------------------");
//		System.out.println();
//
//		exp = new ABinopExp(new ABooleanConstExp(new ATrueBoolean()), new ALazyAndBinop(new TAndAnd()), new AIntConstExp(new TNumbersLiteral("5")));
//
//		if (check(exp))
//		{
//			eval(exp);
//		}
//
//		customFieldTest(exp);
//	}
//
//	public static void customFieldTest(PExp exp)
//	{
//		try{
//		String pluginId ="org.overture.ide.plugins.codegeneration";
//		String field="javaType";
//		exp.setCustomField(pluginId, field, String.class);
//		@SuppressWarnings("rawtypes")
//		Class javaClass = exp.getCustomField(pluginId, field,Object.class);
//		System.out.println("Test customfield for "+ pluginId+"."+field+"="+javaClass);
//		}catch(ClassCastException e)
//		{
//			
//		}
//	}
//
//	public static boolean check(PExp exp)
//	{
//		System.out.println("Checking: " + exp);
//		PType n = exp.typeCheck(typeChecker, new Environment(), new NameScope(), null);
//		System.out.println("Type is: " + n);
//		return n != null;
//	}
//
//	public static void eval(PExp exp)
//	{
//		System.out.println();
//		System.out.println("Evaluating:" + exp);
//		IValue n = exp.eval(new Eval(new PBinopEval(), new PUnopEval(), new CustomPExpEval(), new PBooleanEval(), new PTypeEval()), null);
//		System.out.print(" = " + n);
//		System.out.println();
//	}
//
//}
