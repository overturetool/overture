package org.overture.codegen.assistant;


public class ExpAssistantCG
{	
//	private ExpVisitorCG expVisitor;
//	private OperatorLookup opLookup;
//	
//	public ExpAssistantCG(ExpVisitorCG expVisitor, OperatorLookup opLookup)
//	{
//		this.expVisitor = expVisitor;
//		this.opLookup = opLookup;
//	}
//
//	public String formatExp(SNumericBinaryExp parent, PExp child, CodeGenContextMap question) throws AnalysisException
//	{
//		boolean wrap = childExpHasLowerPrecedence(parent, child);
//
//		String unwrapped = child.apply(expVisitor, question);
//		
//		return wrap ? "(" + unwrapped + ")" : unwrapped; 
//	}
//	
//	public boolean isIntegerType(PExp exp)
//	{	
//		PType type = exp.getType();
//		
//		return (type instanceof ANatOneNumericBasicType 
//				|| type instanceof ANatNumericBasicType
//				|| type instanceof AIntNumericBasicType) 
//				&& !(exp instanceof ARealLiteralExp); //Expressions like 1.0 are considered real literal expressions of type NatOneNumericBasicType
//	}
//		
//	public boolean childExpHasLowerPrecedence(SNumericBinaryExp parent, PExp child)
//	{				
//		if(!(child instanceof SBinaryExp))
//			return false;
//
//		OperatorInfo parentOpInfo = opLookup.find(parent.getClass());
//		
//		SBinaryExp binExpChild = (SBinaryExp) child;
//		OperatorInfo childInfo = opLookup.find(binExpChild.getClass());
//		
//		return childInfo.getPrecedence() < parentOpInfo.getPrecedence();
//	}
}
