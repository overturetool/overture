package org.overture.pog.contexts;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AExternalClause;
import org.overture.pog.pub.IPOContext;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.IVariableSubVisitor;

public class OpPostConditionContext extends StatefulContext implements
		IPOContext
{

	AExistsExp exists_exp;
	PExp pred;
	IVariableSubVisitor visitor;

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			ACallStm stm, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, IPOContextStack ctxt)
	{
		super(ctxt);
		this.gen = ctxt.getGenerator();
		this.subs = new LinkedList<Substitution>();
		this.exists_exp = getChangedVarsExp(postDef, calledOp);
		this.pred = spellCondition(postDef, af, stm.getArgs());
		this.visitor = af.getVarSubVisitor();
	}

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			AApplyExp exp, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, IPOContextStack ctxt)
	{
		super(ctxt);
		this.visitor = af.getVarSubVisitor();
		this.gen = ctxt.getGenerator();
		this.subs = new LinkedList<Substitution>();
		this.exists_exp = getChangedVarsExp(postDef, calledOp);
		this.pred = spellCondition(postDef, af, exp.getArgs());
	}

	@Override
	public String toString()
	{
		return exists_exp.toString() + pred.toString();
	}

	private AExistsExp getChangedVarsExp(AExplicitFunctionDefinition postDef,
			SOperationDefinitionBase calledOp)
	{
		AExistsExp r = new AExistsExp();
		List<PMultipleBind> binds = new LinkedList<PMultipleBind>();

		if (calledOp instanceof AExplicitOperationDefinition)
		{
			refreshAllState(calledOp, binds);
		}

		if (calledOp instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinition implicitOp = (AImplicitOperationDefinition) calledOp;
			if (implicitOp.getExternals().size() > 0)
			{
				for (AExternalClause external : implicitOp.getExternals())
				{
					if (external.getMode().getType().equals(VDMToken.WRITE))
					{
						binds.addAll(introduceFreshVars(external.getIdentifiers(), getStateVars(calledOp)));
					}
				}

			} else
			{
				refreshAllState(calledOp, binds);
			}
		}

		r.setBindList(binds);
		return r;
	}

	private List<AInstanceVariableDefinition> getStateVars(
			SOperationDefinitionBase calledOp)
	{
		List<PDefinition> defs;
		if (calledOp.getClassDefinition() != null)
		{
			defs = calledOp.getClassDefinition().getDefinitions();
		} else
		{
			if (calledOp.getState() != null)
			{
				defs = calledOp.getState().getStateDefs();
			} else
				return new LinkedList<AInstanceVariableDefinition>();
		}
		List<AInstanceVariableDefinition> r = new LinkedList<AInstanceVariableDefinition>();
		for (PDefinition p : defs)
		{
			if (p instanceof AInstanceVariableDefinition)
			{
				r.add((AInstanceVariableDefinition) p);
			}
		}
		return r;
	}

	private void refreshAllState(SOperationDefinitionBase calledOp,
			List<PMultipleBind> binds)
	{
		List<AInstanceVariableDefinition> defs = getStateVars(calledOp);

		for (AInstanceVariableDefinition p : defs)
		{
			binds.add(introduceFreshVar(p));
		}
	}

	private Collection<? extends PMultipleBind> introduceFreshVars(
			LinkedList<ILexNameToken> identifiers,
			List<AInstanceVariableDefinition> defs)
	{
		List<PMultipleBind> r = new LinkedList<PMultipleBind>();
		for (ILexNameToken ilt : identifiers)
		{
			for (AInstanceVariableDefinition d : defs)
			{
				if (ilt.equals(d.getName()))
				{
					r.add(introduceFreshVar(d));
				}
			}

		}
		return r;
	}

	@Override
	public String getContext()
	{
		return null;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		try
		{
			if (isLast())
			{
				for (Substitution sub : subs)
				{
					if (sub.getOriginal().contains("$OLD"))
					{						
						stitch=stitch.apply(visitor,sub);	// nothing;
					} else
					{
						stitch = stitch.apply(visitor, sub);
					}
				}

				// do nothing
			} else
			{
				for (Substitution sub : subs)
				{
					stitch = stitch.apply(visitor, sub);
				}
			}

			for (Substitution sub : subs)
			{
				pred = pred.apply(visitor, sub);
			}

			PExp implies_exp = AstExpressionFactory.newAImpliesBooleanBinaryExp(pred.clone(), stitch.clone());

			if (exists_exp.getBindList().size() > 0)
			{

				exists_exp.setPredicate(implies_exp);
				return exists_exp.clone();
			} else
			{
				return implies_exp.clone();
			}
		} catch (AnalysisException e)
		{
			// FIXME consider handling of exceptions inside final context construction
			e.printStackTrace();
		}
		return null;
	}

	private PExp spellCondition(AExplicitFunctionDefinition def,
			IPogAssistantFactory af, List<PExp> args)
	{
		if (def == null)
		{
			// no postcondition: true
			ABooleanConstExp r = new ABooleanConstExp();
			r.setValue(new LexBooleanToken(true, null));
			return r;
		}
		List<Substitution> subs = new LinkedList<Substitution>();

		for (int i = 0; i < args.size(); i++)
		{
			PPattern orig = def.getParamPatternList().get(0).get(i);
			ILexNameToken origName = af.createPPatternAssistant().getAllVariableNames(orig).get(0).clone();
			PExp new_exp = args.get(0).clone();
			subs.add(new Substitution(origName, new_exp));
		}
		return rewritePost(def, subs, af);
	}

	private PExp rewritePost(AExplicitFunctionDefinition def,
			List<Substitution> subs, IPogAssistantFactory af)
	{
		PExp post_exp = def.getBody();

		if (post_exp instanceof APostOpExp)
		{
			// post-expression bodies are wrapped in a PostOpExp for some reason...
			post_exp = ((APostOpExp) post_exp).getPostexpression();
		}

		for (Substitution sub : subs)
		{
			try
			{
				post_exp = post_exp.apply(af.getVarSubVisitor(), sub);
			} catch (AnalysisException e)
			{

				e.printStackTrace();
			}
		}

		return post_exp;
	}

	public Map<ILexNameToken, AVariableExp> getLast_vars()
	{
		return last_vars;
	}

}
