package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.pog.obligation.POContext;
import org.overture.pog.pub.IPogAssistantFactory;

public class AssignmentContext extends POContext
{

	Substitution sub;
	IVariableSubVisitor visitor;

	@Override
	public String getContext()
	{
		return null;
	}

	public AssignmentContext(AAssignmentStm node, IVariableSubVisitor visitor,
			IPogAssistantFactory af) throws AnalysisException
	{

		String hash = node.getTarget().apply(af.createStateDesignatorNameGetter());

		sub = new Substitution(hash, node.getExp());
		this.visitor = visitor;

	}

	@Override
	public boolean isStateful()
	{
		return true;
	}

	public AssignmentContext(AInstanceVariableDefinition node,
			IVariableSubVisitor visitor)
	{
		sub = new Substitution(node.getName(), node.getExpression());
		this.visitor = visitor;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{

		PExp r;
		try
		{
			r = stitch.apply(visitor, sub);
			return r;
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
