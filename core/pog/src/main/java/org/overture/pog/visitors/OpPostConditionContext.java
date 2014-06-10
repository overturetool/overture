package org.overture.pog.visitors;

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
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.pog.obligation.POContext;
import org.overture.pog.pub.IPOContext;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.UniqueNameGenerator;

public class OpPostConditionContext extends POContext implements IPOContext
{

	AForAllExp forAll_exp;
	PExp pred;
	UniqueNameGenerator gen;
	List<Substitution> subs;
	IVariableSubVisitor visitor;

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			ACallStm stm, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, UniqueNameGenerator gen,
			IVariableSubVisitor visitor)
	{
		this.gen = gen;
		this.subs = new LinkedList<Substitution>();
		this.forAll_exp = getChangedVarsExp(postDef, calledOp);
		this.pred = spellCondition(postDef, af, stm.getArgs());
		this.visitor = visitor;
	}

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			AApplyExp exp, SOperationDefinitionBase calledOp,
			IPogAssistantFactory af, UniqueNameGenerator gen,
			IVariableSubVisitor visitor)
	{
		this.visitor = visitor;
		this.subs = new LinkedList<Substitution>();
		this.gen = gen;
		this.forAll_exp = getChangedVarsExp(postDef, calledOp);
		this.pred = spellCondition(postDef, af, exp.getArgs());
	}
	
	@Override
	public String toString()
	{
		return forAll_exp.toString() + pred.toString();
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

			} else
			{
				refreshAllState(calledOp, binds);
			}
		}

		r.setBindList(binds);
		return r;
	}

	private void refreshAllState(SOperationDefinitionBase calledOp,
			List<PMultipleBind> binds)
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
				defs = new LinkedList<PDefinition>();
		}

		for (PDefinition p : defs)
		{
			if (p instanceof AInstanceVariableDefinition)
			{
				binds.add(introduceFreshVar((AInstanceVariableDefinition) p));
			}
		}
	}

	PMultipleBind introduceFreshVar(AInstanceVariableDefinition var)
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

		return r;
	}

	@Override
	public boolean isStateful()
	{
		return true;
	}

	@Override
	public String getContext()
	{
		return null;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		PExp implies_exp = AstExpressionFactory.newAImpliesBooleanBinaryExp(pred.clone(), stitch.clone());
		for (Substitution sub : subs)
		{
			try
			{
				implies_exp = implies_exp.apply(visitor, sub);
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (forAll_exp.getBindList().size() > 0)
		{

			forAll_exp.setPredicate(implies_exp);
			return forAll_exp.clone();
		} else
		{
			return implies_exp.clone();
		}
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
			PPattern orig = def.getParamPatternList().get(0).get(1).clone();
			ILexNameToken origName = af.createPPatternAssistant().getAllVariableNames(orig).get(0).clone();
			PExp new_exp = args.get(0).clone();
			subs.add(new Substitution(origName, new_exp));
		}
		return rewriteExp(def, subs);
	}

	// FIXME unify expression rewrite method with the one from
	// OperationCallObligation
	private PExp rewriteExp(AExplicitFunctionDefinition def,
			List<Substitution> subs)
	{
		PExp pre_exp = def.getBody().clone();

		for (Substitution sub : subs)
		{
			try
			{
				pre_exp = pre_exp.apply(new VariableSubVisitor(), sub);
			} catch (AnalysisException e)
			{

				e.printStackTrace();
			}
		}

		return pre_exp;
	}

}
