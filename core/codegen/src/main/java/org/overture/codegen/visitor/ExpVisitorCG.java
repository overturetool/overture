package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SNumericBinaryExp;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.operators.OperatorLookup;

public class ExpVisitorCG extends QuestionAnswerAdaptor<CodeGenContextMap, String>
{
	private static final long serialVersionUID = -7481045116217669686L;
	
	private CodeGenVisitor rootVisitor;
	private OperatorLookup opLookup;
	
	private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG(CodeGenVisitor rootVisitor)
	{
		this.rootVisitor = rootVisitor;
		this.opLookup = OperatorLookup.GetInstance();
		expAssistant = new ExpAssistantCG(this, opLookup);
	}
	
	@Override
	public String defaultSNumericBinaryExp(SNumericBinaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		String left = expAssistant.formatExp(node, node.getLeft(), question);
		String operator = opLookup.find(node.getClass()).getMapping();
		String right = expAssistant.formatExp(node, node.getRight(), question);
			
		return left + " " + operator + " " + right;
	}
	
	//Unary
	
	@Override
	public String caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		return "+" + node.getExp().apply(this, question);
	}
	
	@Override
	public String caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		return "-" + node.getExp().apply(this, question);
	}

	//Numeric binary
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		PExp leftNode = node.getLeft();
		PExp rightNode = node.getRight();
		
		if(expAssistant.isIntegerType(leftNode) && expAssistant.isIntegerType(rightNode))
		{
			//We wrap it as a double because expressions like 1/2 equals 0 in Java whereas 1/2 = 0.5 in VDM	
			String left = "new Double(" + expAssistant.formatExp(node, node.getLeft(), question) + ")";
			String operator = opLookup.find(node.getClass()).getMapping();
			String right = expAssistant.formatExp(node, node.getRight(), question);
				
			return left + " " + operator + " " + right;
		}
		
		return super.caseADivideNumericBinaryExp(node, question);
	}
	
	@Override
	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		PExp leftNode = node.getLeft();
		PExp rightNode = node.getRight();
		
		String left = expAssistant.formatExp(node, leftNode, question);
		String operator = opLookup.find(node.getClass()).getMapping();
		String right = expAssistant.formatExp(node, rightNode, question);
		
		
		if(!expAssistant.isIntegerType(leftNode))
			left = "new Double(" + leftNode + ").intValue()";
		
		if(!expAssistant.isIntegerType(rightNode))
			right = "new Double(" + rightNode + ").intValue()";
			
		return left + " " + operator + " " + right;
	}
	
	//Literal EXP:
	
	@Override
	public String caseARealLiteralExp(ARealLiteralExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		return node.getValue().toString();
		
		//Same as the two below:
		//return node.getValue().toString() + "d";
		//return "new Double(" + node.getValue().toString() + ")";
		//Example: float f = 1.1; "Can't convert double to float
	}

	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node,
			CodeGenContextMap question) throws AnalysisException
	{
		return node.getValue().toString();
		
		//Same as: 
		//return "new Integer(" + node.getValue().toString() + ")";
		//Example: byte b = 1234; "Can't convert int to byte"
	}
}
