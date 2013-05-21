package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.lookup.OperatorLookup;
import org.overture.codegen.lookup.TypeLookup;

public class ExpVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PExpCG>
{
	private static final long serialVersionUID = -7481045116217669686L;
	
	private OperatorLookup opLookup;
	
	private TypeLookup typeLookup;
	
	//private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG()
	{
		this.opLookup = OperatorLookup.GetInstance();
		this.typeLookup = new TypeLookup();
		//expAssistant = new ExpAssistantCG(this, opLookup);
	}
	
	@Override
	public PExpCG defaultPExp(PExp node, CodeGenInfo question)
			throws AnalysisException
	{
		System.out.println("Got in default case!");
		
		return null;
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
		ATimesNumericBinaryExpCG mulExp = new ATimesNumericBinaryExpCG();
		
		mulExp.setType(typeLookup.getType(node.getType()));
		mulExp.setLeft(node.getLeft().apply(this, question));
		mulExp.setRight(node.getRight().apply(this, question));
		
		return mulExp;
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		APlusNumericBinaryExpCG plusExp = new APlusNumericBinaryExpCG();
			
		plusExp.setType(typeLookup.getType(node.getType()));
		plusExp.setLeft(node.getLeft().apply(this, question));
		plusExp.setRight(node.getRight().apply(this, question));
		
		return plusExp;
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		
		ASubtractNumericBinaryExpCG minusExp = new ASubtractNumericBinaryExpCG();
		
		minusExp.setType(typeLookup.getType(node.getType()));
		minusExp.setLeft(node.getLeft().apply(this, question));
		minusExp.setRight(node.getRight().apply(this, question));
		
		return minusExp;
	}
	
	
	@Override
	public PExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divideExp = new ADivideNumericBinaryExpCG();
		
		divideExp.setType(typeLookup.getType(node.getType()));
		divideExp.setLeft(node.getLeft().apply(this, question));
		divideExp.setRight(node.getRight().apply(this, question));
		
		return divideExp;
	}
	
	//Unary
	
	@Override
	public PExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, CodeGenInfo question) throws AnalysisException
	{
		APlusUnaryExpCG unaryPlus = new APlusUnaryExpCG();
		
		unaryPlus.setType(typeLookup.getType(node.getType()));
		unaryPlus.setExp(node.getExp().apply(this, question));
		
		return unaryPlus;
	}

	@Override
	public PExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AMinusUnaryExpCG unaryMinus = new AMinusUnaryExpCG();
		
		unaryMinus.setType(typeLookup.getType(node.getType()));
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
		
		realLiteral.setType(typeLookup.getType(node.getType()));
		realLiteral.setValue(node.getValue().toString());
		
		return realLiteral;
	}
	
	@Override
	public PExpCG caseAIntLiteralExp(AIntLiteralExp node,
			CodeGenInfo question) throws AnalysisException
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		
		intLiteral.setType(typeLookup.getType(node.getType()));
		intLiteral.setValue(node.getValue().toString());
		
		return intLiteral;
	}
	
	@Override
	public PExpCG caseACharLiteralExp(ACharLiteralExp node, CodeGenInfo question)
			throws AnalysisException
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		
		charLiteral.setType(typeLookup.getType(node.getType()));
		charLiteral.setValue(node.getValue().getValue() + "");
		
		return charLiteral;
	}
}
