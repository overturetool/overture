package org.overture.pog.visitors;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.node.INode;
import org.overture.pog.utility.Substitution;

//FIXME complete the variable substitution visitor
public class VariableSubVisitor extends
		QuestionAnswerAdaptor<Substitution, PExp> implements
		IVariableSubVisitor
{

	private IVariableSubVisitor main;

	public VariableSubVisitor()
	{
		main = this;
	}

	public VariableSubVisitor(IVariableSubVisitor main)
	{
		this.main = main;
	}

	// Let's try to do most stuff with S family

	@Override
	public PExp defaultSBinaryExp(SBinaryExp node, Substitution question)
			throws AnalysisException
	{
		PExp subl = node.getLeft().clone().apply(main, question);
		PExp subr = node.getRight().clone().apply(main, question);
		node.setLeft(subl.clone());
		node.setRight(subr.clone());
		return node;
	}

	@Override
	public PExp defaultSUnaryExp(SUnaryExp node, Substitution question)
			throws AnalysisException
	{
		PExp sub = node.getExp().clone().apply(main, question);
		node.setExp(sub.clone());
		return node;
	}

	// cases here for what we need

	@Override
	public PExp caseAExistsExp(AExistsExp node, Substitution question)
			throws AnalysisException
	{
		PExp sub = node.getPredicate().clone().apply(main, question);
		node.setPredicate(sub.clone());
		return node;

	}

	@Override
	public PExp caseAFieldExp(AFieldExp node, Substitution question)
			throws AnalysisException
	{
		PExp obj = node.getObject().clone().apply(main, question);
		node.setObject(obj.clone());
		return node;
	}

	@Override
	public PExp caseAPostOpExp(APostOpExp node, Substitution question)
			throws AnalysisException
	{
		PExp sub = node.getPostexpression().clone().apply(main, question);
		node.setPostexpression(sub.clone());
		return node;
	}

	@Override
	public PExp caseAVariableExp(AVariableExp node, Substitution question)
			throws AnalysisException
	{
		if (question.containsKey(node))
		{
			return question.get(node).clone();
		}
		return node;
	}

	@Override
	public PExp caseAApplyExp(AApplyExp node, Substitution question)
			throws AnalysisException
	{
		node.setArgs(distribute(node.getArgs(), question));
		return node;
	}

	@Override
	public PExp caseAMkTypeExp(AMkTypeExp node, Substitution question)
			throws AnalysisException
	{
		node.setArgs(distribute(node.getArgs(), question));
		return node;
	}

	@Override
	public PExp defaultPExp(PExp node, Substitution question)
			throws AnalysisException
	{
		return node;
	}

	@Override
	public PExp createNewReturnValue(Object arg0, Substitution arg1)
			throws AnalysisException
	{
		throw new AnalysisException("Substitution visitor applied to non-expression object "
				+ arg0.toString());
	}

	@Override
	public PExp createNewReturnValue(INode arg0, Substitution arg1)
			throws AnalysisException
	{
		throw new AnalysisException("Substitution visitor applied to non-expression object "
				+ arg0.toString());
	}

	private List<PExp> distribute(List<PExp> args, Substitution question)
			throws AnalysisException
	{
		List<PExp> subs = new Vector<PExp>();
		for (PExp a : args)
		{
			subs.add(a.clone().apply(main, question));
		}
		return subs;
	}

}
