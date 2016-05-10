package org.overture.interpreter.utilities.expression;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method collects and returns the subexpressions in a expression.
 * 
 * @author gkanos
 ****************************************/
public class SubExpressionsLocator extends AnswerAdaptor<List<PExp>>
{
	protected IInterpreterAssistantFactory af;

	public SubExpressionsLocator(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<PExp> caseAApplyExp(AApplyExp exp) throws AnalysisException
	{
		List<PExp> subs = new ArrayList<PExp>();
		subs.addAll(exp.getRoot().apply(THIS));
		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> defaultSBinaryExp(SBinaryExp exp)
			throws AnalysisException
	{
		List<PExp> subs = exp.getLeft().apply(THIS);
		subs.addAll(exp.getRight().apply(THIS));
		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> caseACaseAlternative(ACaseAlternative exp)
			throws AnalysisException
	{
		return exp.getResult().apply(THIS);
	}

	@Override
	public List<PExp> caseACasesExp(ACasesExp exp) throws AnalysisException
	{
		List<PExp> subs = exp.getExpression().apply(THIS);

		for (ACaseAlternative c : exp.getCases())
		{
			subs.addAll(c.apply(THIS));
		}

		if (exp.getOthers() != null)
		{
			subs.addAll(exp.getOthers().apply(THIS));
		}

		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> caseAElseIfExp(AElseIfExp exp) throws AnalysisException
	{
		List<PExp> subs = exp.getElseIf().apply(THIS);
		subs.addAll(exp.getThen().apply(THIS));
		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> caseAIfExp(AIfExp exp) throws AnalysisException
	{
		List<PExp> subs = exp.getTest().apply(THIS);
		subs.addAll(exp.getThen().apply(THIS));

		for (AElseIfExp elif : exp.getElseList())
		{
			subs.addAll(elif.apply(THIS));
		}

		if (exp.getElse() != null)
		{
			subs.addAll(exp.getElse().apply(THIS));
		}

		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> defaultPExp(PExp exp) throws AnalysisException
	{
		List<PExp> subs = new ArrayList<PExp>();
		subs.add(exp);
		return subs;
	}

	@Override
	public List<PExp> createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PExp> createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
