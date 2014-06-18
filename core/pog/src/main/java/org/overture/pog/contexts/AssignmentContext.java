package org.overture.pog.contexts;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.IVariableSubVisitor;

public class AssignmentContext extends StatefulContext
{

	List<Substitution> subs;
	IVariableSubVisitor visitor;

	public AssignmentContext(AAssignmentStm node, IPogAssistantFactory af,
			IPOContextStack ctxt) throws AnalysisException
	{
		super(ctxt);
		subs = new LinkedList<Substitution>();
		String hash = node.getTarget().apply(af.getStateDesignatorNameGetter());

		ILexNameToken t = new LexNameToken("", hash, null);

		boolean found = false;
		for (ILexNameToken n : last_vars.keySet())
		{
			if (n.getName().equals(hash))
			{
				AVariableExp var_exp = last_vars.get(n);
				subs.add(new Substitution(var_exp.getName(), node.getExp()));
				subs.add(new Substitution(var_exp.getName().getOldName().clone(), var_exp.clone()));
				found = true;
			}
			break;
		}
		if (!found)
		{
			subs.add(new Substitution(t, node.getExp()));
		}
		this.visitor = af.getVarSubVisitor();
	}

	public AssignmentContext(AInstanceVariableDefinition node,
			IVariableSubVisitor visitor, IPOContextStack ctxt)
	{
		super(ctxt);
		subs = new LinkedList<Substitution>();
		subs.add(new Substitution(node.getName(), node.getExpression()));
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

		PExp r = null;
		try
		{
			for (Substitution sub : subs)
			{
				r = stitch.apply(visitor, sub);
			}
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
		return subs.toString();
	}

}
