package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.lookup.TypeLookup;

public class ExpVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PExpCG>
{
	private static final long serialVersionUID = -7481045116217669686L;
	
	private TypeLookup typeLookup;
	
	private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG()
	{
		this.typeLookup = new TypeLookup();
		this.expAssistant = new ExpAssistantCG(this);
	}
		
	@Override
	public PExpCG caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ATimesNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new APlusNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ASubtractNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, CodeGenInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new AGreaterEqualNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return expAssistant.handleBinaryExp(node, new AGreaterNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, CodeGenInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessEqualNumericBinaryExpCG(), question, typeLookup);
	}
	
	
	@Override
	public PExpCG caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			CodeGenInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divideExp = new ADivideNumericBinaryExpCG();
		
		divideExp.setType(typeLookup.getType(node.getType()));
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		PExpCG leftExpCG = leftExp.apply(this, question);
		PExpCG rightExpCG = rightExp.apply(this, question);
		
		if(expAssistant.isIntegerType(leftExp) && expAssistant.isIntegerType(rightExp))
		{
			ACastUnaryExpCG castExpr = new ACastUnaryExpCG();
			castExpr.setType(new ARealNumericBasicTypeCG());
			castExpr.setExp(leftExpCG);
			leftExpCG = castExpr;
		}
		
		divideExp.setLeft(leftExpCG);
		divideExp.setRight(rightExpCG);
		
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
		//TODO: Optimize similar literal expressions
		//Put the similar code in an assistant
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
