package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.operators.OperatorInfo;
import org.overture.codegen.operators.OperatorLookup;
import org.overture.codegen.visitor.CodeGenContextMap;
import org.overture.codegen.visitor.ExpVisitorCG;

public class ExpAssistantCG
{	
	private ExpVisitorCG expVisitor;
	private OperatorLookup opLookup;
	
	public ExpAssistantCG(ExpVisitorCG expVisitor, OperatorLookup opLookup)
	{
		this.expVisitor = expVisitor;
		this.opLookup = opLookup;
	}

	public String formatExp(SNumericBinaryExp parent, PExp child, CodeGenContextMap question) throws AnalysisException
	{
		boolean wrap = childExpHasLowerPrecedence(parent, child);
		
		String unwrapped = child.apply(expVisitor, question);
		
		return wrap ? "(" + unwrapped + ")" : unwrapped; 
	}
	
	public boolean isIntegerType(PExp exp)
	{	
		PType type = exp.getType();
		
		return (type instanceof ANatOneNumericBasicType 
				|| type instanceof ANatNumericBasicType
				|| type instanceof AIntNumericBasicType) 
				&& !(exp instanceof ARealLiteralExp); //Expressions like 1.0 are considered real literal expressions of type NatOneNumericBasicType
	}
		
	public boolean childExpHasLowerPrecedence(SNumericBinaryExp parent, PExp child)
	{				
		if(!(child instanceof SBinaryExp))
			return false;

		OperatorInfo parentOpInfo = opLookup.find(parent.getClass());
		
		SBinaryExp binExpChild = (SBinaryExp) child;
		OperatorInfo childInfo = opLookup.find(binExpChild.getClass());
		
		return childInfo.getPrecedence() < parentOpInfo.getPrecedence();
	}
}
