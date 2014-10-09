package org.overture.pog.visitors;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;

public class VdmInvExpGetVisitor extends AnswerAdaptor<List<PExp>> implements
		IInvExpGetVisitor
{

	IInvExpGetVisitor mainVisitor;

	public VdmInvExpGetVisitor()
	{
		mainVisitor = this;
	}

	public VdmInvExpGetVisitor(IInvExpGetVisitor mainVisitor)
	{
		this.mainVisitor = mainVisitor;
	}

	@Override
	public List<PExp> createNewReturnValue(INode node) throws AnalysisException
	{
		return new LinkedList<PExp>();
	}

	@Override
	public List<PExp> createNewReturnValue(Object node)
			throws AnalysisException
	{
		return new LinkedList<PExp>();
	}

	@Override
	public List<PExp> caseAClassClassDefinition(AClassClassDefinition node)
			throws AnalysisException
	{
		List<PExp> r = new LinkedList<PExp>();
		for (PDefinition p : node.getDefinitions())
		{
			r.addAll(p.apply(mainVisitor));
		}
		return r;
	}

	@Override
	public List<PExp> caseAClassInvariantDefinition(
			AClassInvariantDefinition node) throws AnalysisException
	{
		List<PExp> r = new LinkedList<PExp>();
		r.add(node.getExpression());
		return r;
	}
}
