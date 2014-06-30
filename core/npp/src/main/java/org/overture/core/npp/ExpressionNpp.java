package org.overture.core.npp;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.node.INode;

class ExpressionNpp extends QuestionAnswerAdaptor<IndentTracker, String>
		implements IPrettyPrinter
{

	InsTable mytable;
	IPrettyPrinter rootNpp;

	private static String EXPRESSION_NOT_FOUND = "ERROR: Expression Node not found";
	private static String space = " ";

	public ExpressionNpp(NewPrettyPrinter root, InsTable nst)
	{
		rootNpp = root;
		mytable = nst;
	}

	@Override
	public void setInsTable(InsTable it)
	{
		mytable = it;
	}

	@Override
	public String caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IndentTracker question) throws AnalysisException
	{
		String l = node.getLeft().apply(this, question);
		String r = node.getRight().apply(this, question);
		String op = mytable.getPlus();

		StringBuilder sb = new StringBuilder();

		sb.append(l);
		sb.append(space);
		sb.append(op);
		sb.append(space);
		sb.append(r);

		return CommonStringManips.wrap(sb.toString());
	}
	

	@Override
	public String caseAIntLiteralExp(AIntLiteralExp node, IndentTracker question)
			throws AnalysisException
	{
		return Long.toString(node.getValue().getValue());
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
