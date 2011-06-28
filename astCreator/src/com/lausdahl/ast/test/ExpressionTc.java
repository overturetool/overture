//package com.lausdahl.ast.test;
//
//import com.lausdahl.runtime.Environment;
//import com.lausdahl.runtime.NameScope;
//import com.lausdahl.runtime.TypeList;
//
//import generated.node.ABinopExp;
//import generated.node.ABoolType;
//import generated.node.ABooleanConstExp;
//import generated.node.AIntConstExp;
//import generated.node.AIntType;
//import generated.node.ALazyAndBinop;
//import generated.node.ALazyOrBinop;
//import generated.node.AMinusBinop;
//import generated.node.APlusBinop;
//import generated.node.Node;
//import generated.node.PExpTypeChecker;
//import generated.node.PType;
//
//public class ExpressionTc extends PExpTypeChecker
//{
//	@Override
//	public <Typ extends Node> Typ caseAIntConstExp(AIntConstExp source,
//			Environment env, NameScope scope, TypeList qualifiers)
//	{
//		try
//		{
//			Integer.parseInt(source.getNumbersLiteral().getText());
//			source.setType(new AIntType());
//			return (Typ) source.getType();
//		} catch (Exception e)
//		{
//
//		}
//		return null;
//	}
//
//	@Override
//	public <Typ extends Node> Typ caseABooleanConstExp(ABooleanConstExp source,
//			Environment env, NameScope scope, TypeList qualifiers)
//	{
//		source.setType(new ABoolType());
//		return (Typ) source.getType();
//	}
//	
//	@Override
//	public <Typ extends Node> Typ caseABinopExp(ABinopExp source,
//			Environment env, NameScope scope, TypeList qualifiers)
//	{
//		PType expected = null;
//		
//		if(source.getBinop() instanceof APlusBinop || source.getBinop() instanceof AMinusBinop)
//		{
//			expected = new AIntType();
//		}else if( source.getBinop() instanceof ALazyAndBinop ||source.getBinop() instanceof ALazyOrBinop)
//		{
//			expected = new ABoolType();
//		}
//		
//		Node ltype = source.getLeft().typeCheck(this.parent(), env, scope, null);
//		Node rtype = source.getRight().typeCheck(this.parent(), env, scope, null);
//
//		if (!expected.getClass().isInstance(ltype))
//		{
//			report(3065, "Left hand of " + source.getBinop() + " is not "
//					+ expected);
//		}
//
//		if (!expected.getClass().isInstance(rtype))
//		{
//			report(3066, "Right hand of " + source.getBinop() + " is not "
//					+ expected);
//		}
//		source.setType((PType) expected);
//		return (Typ) expected;
//	}
//
//	private void report(Integer i, String string)
//	{
//		System.err.println(i + ": " + string);
//		System.err.flush();
//	}
//}
