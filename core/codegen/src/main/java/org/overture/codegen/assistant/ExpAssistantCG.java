package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.lookup.OperatorInfo;
import org.overture.codegen.lookup.OperatorLookup;
import org.overture.codegen.lookup.TypeLookup;
import org.overture.codegen.visitor.ExpVisitorCG;
import org.overture.codegen.visitor.OoAstInfo;


public class ExpAssistantCG
{	
	private OperatorLookup opLookup;
	
	public ExpAssistantCG(ExpVisitorCG expVisitor)
	{
		this.opLookup = new OperatorLookup();
	}
	
	public static PExpCG isolateExpression(PExpCG exp)
	{
		AIsolationUnaryExpCG isolationExp = new AIsolationUnaryExpCG();
		isolationExp.setExp(exp);
		isolationExp.setType(exp.getType());
		return isolationExp;
	}
	
	public PExpCG handleBinaryExp(SBinaryExp vdmExp, SBinaryExpCG codeGenExp, OoAstInfo question, TypeLookup typeLookup) throws AnalysisException
	{	
		codeGenExp.setType(typeLookup.getType(vdmExp.getType()));
		
		//Set the expressions
		codeGenExp.setLeft(formatExp(vdmExp, vdmExp.getLeft(), true, question));
		codeGenExp.setRight(formatExp(vdmExp, vdmExp.getRight(), false, question));
		
		//Set the expression types:
		PType leftVdmType = vdmExp.getLeft().getType();
		codeGenExp.getLeft().setType(typeLookup.getType(leftVdmType));
		PType rightVdmType = vdmExp.getRight().getType();
		codeGenExp.getRight().setType(typeLookup.getType(rightVdmType));
		
		return codeGenExp;
	}
	
	public PExpCG formatExp(SBinaryExp parent, PExp child, boolean leftChild, OoAstInfo question) throws AnalysisException
	{
		
		PExpCG exp = child.apply(question.getExpVisitor(), question);
		
		if(child instanceof SBinaryExp)
		{
			OperatorInfo parentOpInfo = opLookup.find(parent.getClass());
			SBinaryExp binExpChild = (SBinaryExp) child;
			OperatorInfo childInfo = opLookup.find(binExpChild.getClass());
			
			//Case 1: Protect against cases like 1 / (2*3*4)
			//Don't care about left children, i.e. (2*3*4)/1 = 2*3*4/1
			boolean case1 = !leftChild && parent instanceof ADivideNumericBinaryExp &&
						 	parentOpInfo.getPrecedence() >= childInfo.getPrecedence();
			
		    //Case 2: Protect against case like 1 / (1+2+3)
			boolean case2 = parentOpInfo.getPrecedence() > childInfo.getPrecedence(); 
			
			if	( case1 || case2 )
			{
				AIsolationUnaryExpCG isolatioNExp = new AIsolationUnaryExpCG();
				isolatioNExp.setType(exp.getType());
				isolatioNExp.setExp(exp);
				exp = isolatioNExp;
			}
		}
		
		return exp;
	}
	
	public boolean isIntegerType(PExp exp)
	{	
		PType type = exp.getType();
		
		return (type instanceof ANatOneNumericBasicType 
				|| type instanceof ANatNumericBasicType
				|| type instanceof AIntNumericBasicType) 
				&& !(exp instanceof ARealLiteralExp);
		//Expressions like 1.0 are considered real literal expressions
		//of type NatOneNumericBasicType
	}
	
}
