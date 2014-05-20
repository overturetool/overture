package org.overture.interpreter.utilities.expression;

import java.util.List;
import java.util.Vector;

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
import org.overture.interpreter.assistant.expression.AApplyExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACaseAlternativeAssistantInterpreter;
import org.overture.interpreter.assistant.expression.ACasesExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AElseIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.AIfExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.expression.SBinaryExpAssistantInterpreter;
/***************************************
 * 
 * This method collects and returns the subexpressions in a expression.
 * 
 * @author gkanos
 *
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
		//return AApplyExpAssistantInterpreter.getSubExpressions(exp);
		List<PExp> subs = new Vector<PExp>();
		subs.addAll(exp.getRoot().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getRoot()));
		subs.add(exp);
		return subs;
	}
	
	@Override
	public List<PExp> defaultSBinaryExp(SBinaryExp exp)
			throws AnalysisException
	{
		//return SBinaryExpAssistantInterpreter.getSubExpressions(exp);
		List<PExp> subs = exp.getLeft().apply(THIS);//PExpAssistantInterpreter.getSubExpressions(exp.getLeft());
		subs.addAll(exp.getRight().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getRight()));
		subs.add(exp);
		return subs;
	}
	
	@Override
	public List<PExp> caseACaseAlternative(ACaseAlternative exp)
			throws AnalysisException
	{
		//return PExpAssistantInterpreter.getSubExpressions(c.getResult());
		return exp.getResult().apply(THIS);
	}
	
	@Override
	public List<PExp> caseACasesExp(ACasesExp exp) throws AnalysisException
	{
		//return ACasesExpAssistantInterpreter.getSubExpressions(exp);
		List<PExp> subs = exp.getExpression().apply(THIS);//PExpAssistantInterpreter.getSubExpressions(exp.getExpression());

		for (ACaseAlternative c : exp.getCases())
		{
			subs.addAll(c.apply(THIS));//(ACaseAlternativeAssistantInterpreter.getSubExpressions(c));
		}

		if (exp.getOthers() != null)
		{
			subs.addAll(exp.getOthers().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getOthers()));
		}

		subs.add(exp);
		return subs;
	}
	
	@Override
	public List<PExp> caseAElseIfExp(AElseIfExp exp) throws AnalysisException
	{
		//return AElseIfExpAssistantInterpreter.getSubExpressions(exp);
		List<PExp> subs = exp.getElseIf().apply(THIS);//PExpAssistantInterpreter.getSubExpressions(exp.getElseIf());
		subs.addAll(exp.getThen().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getThen()));
		subs.add(exp);
		return subs;
	}
	
	@Override
	public List<PExp> caseAIfExp(AIfExp exp) throws AnalysisException
	{
		//return AIfExpAssistantInterpreter.getSubExpressions(exp);
		List<PExp> subs = exp.getTest().apply(THIS);//PExpAssistantInterpreter.getSubExpressions(exp.getTest());
		subs.addAll(exp.getThen().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getThen()));

		for (AElseIfExp elif : exp.getElseList())
		{
			subs.addAll(elif.apply(THIS));//(AElseIfExpAssistantInterpreter.getSubExpressions(elif));
		}

		if (exp.getElse() != null)
		{
			subs.addAll(exp.getElse().apply(THIS));//(PExpAssistantInterpreter.getSubExpressions(exp.getElse()));
		}

		subs.add(exp);
		return subs;
	}
	
	@Override
	public List<PExp> defaultPExp(PExp exp) throws AnalysisException
	{
		List<PExp> subs = new Vector<PExp>();
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
