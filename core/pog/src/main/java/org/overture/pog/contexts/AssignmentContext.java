package org.overture.pog.contexts;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.IVariableSubVisitor;

import java.util.Map;

public class AssignmentContext extends StatefulContext
{
	Substitution subLast;
	Substitution sub;
	PExp newVal_exp;
	ILexNameToken t;
	IVariableSubVisitor visitor;

	public AssignmentContext(AAssignmentStm node, IPogAssistantFactory af,
			IPOContextStack ctxt) throws AnalysisException
	{
		super(ctxt);
		String hash = node.getTarget().apply(af.getStateDesignatorNameGetter());

		t = null;

		for (ILexNameToken n : last_vars.keySet())
		{
			if (n.getName().equals(hash))
			{
				t = last_vars.get(n).getName().clone();
				break;
			}
		}
		if (t == null)
		{
			t = new LexNameToken("", hash, null);
		}
		subLast = new Substitution(new LexNameToken("", hash, null), node.getExp().clone());
		sub = new Substitution(t, node.getExp().clone());

		this.visitor = af.getVarSubVisitor();
	}

	public AssignmentContext(AInstanceVariableDefinition node,
			IVariableSubVisitor visitor, IPOContextStack ctxt)
	{
		super(ctxt);
		sub = new Substitution(node.getName(), node.getExpression());
		subLast = sub;
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
			if (first)
			{
				r = stitch.apply(visitor, subLast);
				first = false;
			} else
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
		return sub.toString();
	}

}
