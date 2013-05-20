package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.expressions.AIntLiteralCGExp;
import org.overture.codegen.cgast.expressions.AMinusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.AMinusCGUnaryExp;
import org.overture.codegen.cgast.expressions.AMulCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGUnaryExp;
import org.overture.codegen.cgast.expressions.ARealLiteralCGExp;
import org.overture.codegen.cgast.expressions.PExp;
import org.overture.codegen.operators.OperatorLookup;

public class ExpVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PExp>
{
	private static final long serialVersionUID = -7481045116217669686L;
	
	private OperatorLookup opLookup;
	
	//private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG()
	{
		this.opLookup = OperatorLookup.GetInstance();
		//expAssistant = new ExpAssistantCG(this, opLookup);
	}
	
//	@Override
//	public String defaultSNumericBinaryExp(SNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		String left = expAssistant.formatExp(node, node.getLeft(), question);
//		String operator = opLookup.find(node.getClass()).getMapping();
//		String right = expAssistant.formatExp(node, node.getRight(), question);
//			
//		return left + " " + operator + " " + right;
//	}
	
	//Numeric binary expressions
	
	@Override
	public PExp caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AMulCGNumericBinaryExp mulExp = new AMulCGNumericBinaryExp();
		
		mulExp.setLeft(node.getLeft().apply(this, question));
		mulExp.setRight(node.getRight().apply(this, question));
		
		return mulExp;
	}
	
	@Override
	public PExp caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		APlusCGNumericBinaryExp plusExp = new APlusCGNumericBinaryExp();
			
		plusExp.setLeft(node.getLeft().apply(this, question));
		plusExp.setRight(node.getRight().apply(this, question));
		
		return plusExp;
	}
	
	@Override
	public PExp caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		
		AMinusCGNumericBinaryExp minusExp = new AMinusCGNumericBinaryExp();
		
		minusExp.setLeft(node.getLeft().apply(this, question));
		minusExp.setRight(node.getRight().apply(this, question));
		
		return minusExp;
	}
	
	//Unary
	
	@Override
	public PExp caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, CodeGenInfo question) throws AnalysisException
	{
		APlusCGUnaryExp unaryPlus = new APlusCGUnaryExp();
		
		unaryPlus.setExp(node.getExp().apply(this, question));
		
		return unaryPlus;
	}

	@Override
	public PExp caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AMinusCGUnaryExp unaryMinus = new AMinusCGUnaryExp();
		
		unaryMinus.setExp(node.getExp().apply(this, question));
		
		return unaryMinus;
	}
	
//
//	//Numeric binary
//	@Override
//	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	@Override
//	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	@Override
//	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		PExp leftNode = node.getLeft();
//		PExp rightNode = node.getRight();
//		
//		if(expAssistant.isIntegerType(leftNode) && expAssistant.isIntegerType(rightNode))
//		{
//			//We wrap it as a double because expressions like 1/2 equals 0 in Java whereas 1/2 = 0.5 in VDM	
//			String left = "new Double(" + expAssistant.formatExp(node, node.getLeft(), question) + ")";
//			String operator = opLookup.find(node.getClass()).getMapping();
//			String right = expAssistant.formatExp(node, node.getRight(), question);
//				
//			return left + " " + operator + " " + right;
//		}
//		
//		return super.caseADivideNumericBinaryExp(node, question);
//	}
//	
//	@Override
//	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		PExp leftNode = node.getLeft();
//		PExp rightNode = node.getRight();
//		
//		String left = expAssistant.formatExp(node, leftNode, question);
//		String operator = opLookup.find(node.getClass()).getMapping();
//		String right = expAssistant.formatExp(node, rightNode, question);
//		
//		
//		if(!expAssistant.isIntegerType(leftNode))
//			left = "new Double(" + leftNode + ").intValue()";
//		
//		if(!expAssistant.isIntegerType(rightNode))
//			right = "new Double(" + rightNode + ").intValue()";
//			
//		return left + " " + operator + " " + right;
//	}
//	
//	//Literal EXP:
	
	@Override
	public PExp caseARealLiteralExp(ARealLiteralExp node,
			CodeGenInfo question) throws AnalysisException
	{
		
		ARealLiteralCGExp realLiteral = new ARealLiteralCGExp();
		realLiteral.setValue(node.getValue().toString());
		
		return realLiteral;
	}
	
	@Override
	public PExp caseAIntLiteralExp(AIntLiteralExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AIntLiteralCGExp intLiteral = new AIntLiteralCGExp();
		intLiteral.setValue(node.getValue().toString());
		
		return intLiteral;
	}
}
