package org.overture.codegen.assistant;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
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
	
	public PExpCG handleUnaryExp(SUnaryExp vdmExp, SUnaryExpCG codeGenExp, OoAstInfo question, TypeLookup typeLookup) throws AnalysisException
	{
		PExpCG expCg = formatExp(vdmExp.getExp(), question);
		PTypeCG typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);
		
		codeGenExp.setType(typeCg);

		boolean isolate = vdmExp.getExp() instanceof SBinaryExp;
		codeGenExp.setExp(isolate ? isolateExpression(expCg) : expCg);
		
		return codeGenExp;
	}
	
	public PExpCG handleBinaryExp(SBinaryExp vdmExp, SBinaryExpCG codeGenExp, OoAstInfo question, TypeLookup typeLookup) throws AnalysisException
	{	
		codeGenExp.setType(typeLookup.getType(vdmExp.getType()));
		
		//Set the expressions
		codeGenExp.setLeft(formatExp(vdmExp.getLeft(), true, question));
		codeGenExp.setRight(formatExp(vdmExp.getRight(), false, question));
		
		//Set the expression types:
		PType leftVdmType = vdmExp.getLeft().getType();
		codeGenExp.getLeft().setType(typeLookup.getType(leftVdmType));
		PType rightVdmType = vdmExp.getRight().getType();
		codeGenExp.getRight().setType(typeLookup.getType(rightVdmType));
		
		return codeGenExp;
	}
	
	public PExpCG formatExp(PExp exp, OoAstInfo question) throws AnalysisException
	{
		return formatExp(exp, false, question);
	}
	
	public PExpCG formatExp(PExp exp, boolean leftChild, OoAstInfo question) throws AnalysisException
	{
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		INode parentNode = exp.parent();

		if(!(parentNode instanceof PExp))
			return expCg;
		
		PExp parent = (PExp) parentNode;
		
		if (mustIsolate(parent, exp, leftChild))
			return isolateExpression(expCg);
		
		return expCg;
	}
	
	//Should only be called by expressions that are constructed using different abstract syntax
	//constructs like the implies expression
	public boolean mustIsolate(PExp exp)
	{
		INode parentNode = exp.parent();

		if(!(parentNode instanceof PExp))
			return false;
		
		PExp parentExp = (PExp) parentNode;

		//If the parent has precedence then isolate
		return opLookup.find(parentExp.getClass()) != null;
	}
	
	public boolean isolateOnOpPrecedence(INode parent, Class<? extends PExp> child)
	{
		if(!(parent instanceof PExp))
			return false;
		
		PExp parentExp = (PExp) parent;
		
		OperatorInfo parentOpInfo = opLookup.find(parentExp.getClass());

		if (parentOpInfo == null)
			return false;

		OperatorInfo expOpInfo = opLookup.find(child);

		if (expOpInfo == null)
			return false;
		
		return parentOpInfo.getPrecedence() >= expOpInfo.getPrecedence();
	}
	
	public boolean mustIsolate(PExp parentExp, PExp exp, boolean leftChild)
	{
		OperatorInfo parentOpInfo = opLookup.find(parentExp.getClass());

		if (parentOpInfo == null)
			return false;

		OperatorInfo expOpInfo = opLookup.find(exp.getClass());

		if (expOpInfo == null)
			return false;

		// Case 1: Protect against cases like "1 / (2*3*4)"
		// Don't care about left children, i.e. "(2*3*4)/1 = 2*3*4/1"

		// Similar for subtract: "1 - (1+2+3)" and "1+2+3-3"

		// We don't need to consider 'mod' and 'rem' operators since these are constructed
		// using other operators and isolated if needed using the isolation expression
		boolean case1 = !leftChild
				&& (parentExp instanceof ADivideNumericBinaryExp || parentExp instanceof ASubtractNumericBinaryExp)
				&& parentOpInfo.getPrecedence() >= expOpInfo.getPrecedence();

		if(case1)
			return true;
				
		// Case 2: Protect against case like 1 / (1+2+3)
		boolean case2 = parentOpInfo.getPrecedence() > expOpInfo.getPrecedence();
		
		return case2;
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
