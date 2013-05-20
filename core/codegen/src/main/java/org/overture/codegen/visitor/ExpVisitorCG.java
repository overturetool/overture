package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AMinusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMulNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.operators.OperatorLookup;

public class ExpVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PExpCG>
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
	public PExpCG caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AMulNumericBinaryExpCG mulExp = new AMulNumericBinaryExpCG();
		
		mulExp.setLeft(node.getLeft().apply(this, question));
		mulExp.setRight(node.getRight().apply(this, question));
		
		return mulExp;
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		APlusNumericBinaryExpCG plusExp = new APlusNumericBinaryExpCG();
			
		plusExp.setLeft(node.getLeft().apply(this, question));
		plusExp.setRight(node.getRight().apply(this, question));
		
		return plusExp;
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		
		AMinusNumericBinaryExpCG minusExp = new AMinusNumericBinaryExpCG();
		
		minusExp.setLeft(node.getLeft().apply(this, question));
		minusExp.setRight(node.getRight().apply(this, question));
		
		return minusExp;
	}
	
	//Unary
	
	@Override
	public PExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, CodeGenInfo question) throws AnalysisException
	{
		APlusUnaryExpCG unaryPlus = new APlusUnaryExpCG();
		unaryPlus.setExp(node.getExp().apply(this, question));
		
		return unaryPlus;
	}

	@Override
	public PExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AMinusUnaryExpCG unaryMinus = new AMinusUnaryExpCG();
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
	public PExpCG caseARealLiteralExp(ARealLiteralExp node,
			CodeGenInfo question) throws AnalysisException
	{
		
		ARealLiteralExpCG realLiteral = new ARealLiteralExpCG();
		realLiteral.setValue(node.getValue().toString());
		
		return realLiteral;
	}
	
	@Override
	public PExpCG caseAIntLiteralExp(AIntLiteralExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		intLiteral.setValue(node.getValue().toString());
		
		return intLiteral;
	}
	
	@Override
	public PExpCG caseACharLiteralExp(ACharLiteralExp node, CodeGenInfo question)
			throws AnalysisException
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		charLiteral.setValue(node.getValue().getValue() + "");
		
		return charLiteral;
	}
}
