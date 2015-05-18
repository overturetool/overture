package org.overture.pog.contexts;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.Substitution;

public class OpBodyEndContext extends StatefulContext
{
	List<Substitution> subs;
	IPogAssistantFactory af;

	public OpBodyEndContext(List<AInstanceVariableDefinition> state,
			IPogAssistantFactory af)
	{
		this.af = af;
		subs = new LinkedList<Substitution>();
		for (AInstanceVariableDefinition i : state)
		{
			AVariableExp var_exp = new AVariableExp();
			ILexNameToken it = i.getName();
			String safename = it.getName() + "$";
			ILexNameToken safeToken = new LexNameToken(it.getModule(), safename, it.getLocation().clone());
			var_exp.setName(safeToken);
			var_exp.setType(i.getType().clone());
			var_exp.setOriginal(i.getName().getName().toString());
			subs.add(new Substitution(i.getOldname(), var_exp));
		}

	}

	@Override
	public String getContext()
	{
		// not used anymore. Will go out soon
		return null;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		for (Substitution sub : subs)
		{
			try
			{
				stitch = stitch.clone().apply(af.getVarSubVisitor(), sub);
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		return stitch;
	}

}
