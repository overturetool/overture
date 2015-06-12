package org.overture.pog.contexts;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ATypeMultipleBind;
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

	AForAllExp forall_exp;
	PExp pred;
	IVariableSubVisitor visitor;

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			ACallStm stm, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, IPOContextStack ctxt)
	{
		super(ctxt);
		this.gen = ctxt.getGenerator();
		this.subs = new LinkedList<Substitution>();
		this.forall_exp = getChangedVarsExp(postDef, calledOp);
		PExp inv = buildInvExp(calledOp, af);
		this.pred = spellCondition(postDef, af, stm.getArgs(), inv);
		this.visitor = af.getVarSubVisitor();

	}

	private PExp buildInvExp(SOperationDefinitionBase calledOp,
			IPogAssistantFactory af)
	{
		List<PExp> invariants = new LinkedList<PExp>();
		if (calledOp.getClassDefinition() != null)
		{
			try
			{
				invariants = calledOp.getClassDefinition().apply(af.getInvExpGetVisitor());
				if (invariants.size() > 0)
				{

					PExp inv = invariants.get(0).clone();

					for (int i = 1; i < invariants.size(); i++)
					{
						inv = AstExpressionFactory.newAAndBooleanBinaryExp(inv, invariants.get(i).clone());

					}
					return inv;
				} else
				{
					return null;
				}
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			AApplyExp exp, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, IPOContextStack ctxt)
	{
		super(ctxt);
		this.visitor = af.getVarSubVisitor();
		this.gen = ctxt.getGenerator();
		this.subs = new LinkedList<Substitution>();
		this.forall_exp = getChangedVarsExp(postDef, calledOp);
		PExp inv = buildInvExp(calledOp, af);
		this.pred = spellCondition(postDef, af, exp.getArgs(), inv);
	}

	@Override
	public String toString()
	{
		return forall_exp.toString() + pred.toString();
	}

	private AForAllExp getChangedVarsExp(AExplicitFunctionDefinition postDef,
			SOperationDefinitionBase calledOp)
	{
		AForAllExp r = new AForAllExp();
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
			{
				return new LinkedList<AInstanceVariableDefinition>();
			}
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

	private PMultipleBind introduceFreshVar(AInstanceVariableDefinition var)
	{
		ATypeMultipleBind r = new ATypeMultipleBind();

		List<PPattern> pats = new LinkedList<PPattern>();
		AIdentifierPattern idPat = new AIdentifierPattern();

		idPat.setName(gen.getUnique(var.getName().getName()));
		pats.add(idPat);

		r.setPlist(pats);
		r.setType(var.getType().clone());

		AVariableExp newVar = new AVariableExp();
		newVar.setName(idPat.getName().clone());
		newVar.setOriginal(idPat.getName().getFullName());

		Substitution sub = new Substitution(var.getName().clone(), newVar);
		subs.add(sub);

		AVariableExp old_var = last_vars.get(var.getName());
		if (old_var != null)
		{
			Substitution sub_old = new Substitution(var.getOldname().toString(), old_var);
			subs.add(sub_old);
		} else
		{
			AVariableExp var_exp = new AVariableExp();
			var_exp.setName(var.getName().clone());
			var_exp.setType(var.getType().clone());
			var_exp.setOriginal(var.getName().getName());
			Substitution sub_old = new Substitution(var.getOldname().toString(), var_exp);
			subs.add(sub_old);
		}

		last_vars.put(var.getName(), newVar);

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
			if (first)
			{
				first = false;
			}
			PExp implies_exp = AstExpressionFactory.newAImpliesBooleanBinaryExp(pred.clone(), stitch.clone());

			for (Substitution sub : subs)
			{
				implies_exp = implies_exp.clone().apply(visitor, sub);
			}

			if (forall_exp.getBindList().size() > 0)
			{

				forall_exp.setPredicate(implies_exp);
				return forall_exp.clone();
			} else
			{
				return implies_exp.clone();
			}
		} catch (AnalysisException e)
		{
			//consider handling of exceptions inside final context construction
			e.printStackTrace();
		}
		return null;
	}

	private PExp spellCondition(AExplicitFunctionDefinition def,
			IPogAssistantFactory af, List<PExp> args, PExp invariant)
	{
		PExp post_exp;
		if (def == null)
		{
			if (invariant == null)
			{
				// no postcondition: true
				ABooleanConstExp r = new ABooleanConstExp();
				r.setValue(new LexBooleanToken(true, null));
				return r;
			} else
			{
				return invariant;
			}
		} else
		{
			post_exp = def.getBody();
		}

		if (invariant != null)
		{
			post_exp = AstExpressionFactory.newAAndBooleanBinaryExp(post_exp, invariant);
		}

		List<Substitution> subs = new LinkedList<Substitution>();

		for (int i = 0; i < args.size(); i++)
		{
			PPattern orig = def.getParamPatternList().get(0).get(i);
			ILexNameToken origName = af.createPPatternAssistant().getAllVariableNames(orig).get(0).clone();
			PExp new_exp = args.get(0).clone();
			subs.add(new Substitution(origName, new_exp));
		}
		return rewritePost(post_exp, subs, af);
	}

	private PExp rewritePost(PExp post_exp, List<Substitution> subs,
			IPogAssistantFactory af)
	{
		if (post_exp instanceof APostOpExp)
		{
			// post-expression bodies are wrapped in a PostOpExp for some reason...
			post_exp = ((APostOpExp) post_exp).getPostexpression();
		}

		for (Substitution sub : subs)
		{
			try
			{
				post_exp = post_exp.clone().apply(af.getVarSubVisitor(), sub);
			} catch (AnalysisException e)
			{

				e.printStackTrace();
			}
		}

		return post_exp;
	}

}
