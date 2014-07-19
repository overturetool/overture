package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;

class ExpressionNpp extends QuestionAnswerAdaptor<IndentTracker, String>
		implements IPrettyPrinter
{

	ISymbolTable mytable;
	IPrettyPrinter rootNpp;

	private static String EXPRESSION_NOT_FOUND = "ERROR: Expression Node not found";
	private static String space = " ";
	private static String leftcurly = "{";
	private static String rightcurly = "}";

	public ExpressionNpp(NewPrettyPrinter root, ISymbolTable nst)
	{
		rootNpp = root;
		mytable = nst;
	}

	@Override
	public void setInsTable(ISymbolTable it)
	{
		mytable = it;
	}

	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getPLUS();

		StringBuilder sb = new StringBuilder();

		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);

		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getMINUS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getTIMES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIVIDE();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getMOD();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getDIV();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getIMPLIES();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return	Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			IndentTracker question) throws AnalysisException
	{
		
		String l = node.getExp().apply(THIS,question);
		String op = mytable.getABSOLUTE();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(op);
		sb.append(space);
		sb.append(l);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS,question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getAND();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
		
	}
	
	@Override
	public String caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS,question);
		String op = mytable.getOR();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAEqualsBinaryExp(AEqualsBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getEQUALS();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getGT();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getLT();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getGE();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getLE();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
	@Override
	public String caseACompBinaryExp(ACompBinaryExp node, IndentTracker question)
			throws AnalysisException
	{
		String l = node.getLeft().apply(THIS, question);
		String r = node.getRight().apply(THIS, question);
		String op = mytable.getCOMPOSITION();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);
		
		return Utilities.wrap(sb.toString());
	}
	
//	@Override
//	public String caseAMapletExp(AMapletExp node, IndentTracker question)
//			throws AnalysisException
//	{
//		String l = node.getLeft().apply(THIS,question);
//		String r = node.getRight().apply(THIS, question);
//		String op = mytable.getMAPLET();
//		
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append(leftcurly);
//		sb.append(l);
//		sb.append(space);
//		sb.append(op);
//		sb.append(space);
//		sb.append(r);
//		sb.append(rightcurly);
//		
//		return Utilities.wrap(sb.toString());
//		//return sb.toString();
//	}
	
	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, IndentTracker question)
			throws AnalysisException
	{
		return Long.toString(node.getValue().getValue());
	}
	
	@Override
	public String caseARealLiteralExp(ARealLiteralExp node,
			IndentTracker question) throws AnalysisException
	{
		return Double.toString(node.getValue().getValue());
	}
	
	@Override
	public String caseAVariableExp(AVariableExp node, IndentTracker question)
			throws AnalysisException
	{
		String var = node.getOriginal();
		
		return var;
		
	}
	
	@Override
	public String caseABooleanConstExp(ABooleanConstExp node,
			IndentTracker question) throws AnalysisException
	{
		return Boolean.toString(node.getValue().getValue());
	}

	@Override
	public String createNewReturnValue(INode node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND;
	}

	@Override
	public String createNewReturnValue(Object node, IndentTracker question)
			throws AnalysisException
	{
		return EXPRESSION_NOT_FOUND;
	}

}
