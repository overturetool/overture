package org.overture.pog.contexts;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.IVariableSubVisitor;

public class AssignmentContext extends StatefulContext
{

	Substitution sub;
	IVariableSubVisitor visitor;

	public AssignmentContext(AAssignmentStm node, IPogAssistantFactory af) throws AnalysisException
	{

		String hash = node.getTarget().apply(af.getStateDesignatorNameGetter());

		ILexNameToken t = new LexNameToken("", hash, null);
		sub = new Substitution(t, node.getExp());
		
		this.visitor = af.getVarSubVisitor();

	}
	
	public AssignmentContext(AInstanceVariableDefinition node,
			IVariableSubVisitor visitor)
	{
		sub = new Substitution(node.getName(), node.getExpression());
		this.visitor = visitor;
	}

	@Override
	public String getContext()
	{
		return null;
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


	
	@Override
	public String toString()
	{
		return sub.toString();
	}

}
