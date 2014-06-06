package org.overture.pog.visitors;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.pog.obligation.POContext;
import org.overture.pog.pub.IPOContext;
import org.overture.pog.pub.IPogAssistantFactory;

public class OpPostConditionContext extends POContext implements
		IPOContext {

	final PExp exp;

	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			ACallStm stm, IPogAssistantFactory af) {
		PExp spellPre_exp = spellCondition(postDef, af, stm);
		this.exp = spellPre_exp;
	}
	
	public OpPostConditionContext(AExplicitFunctionDefinition postDef,
			AApplyExp exp, IPogAssistantFactory af) {
		PExp spellPre_exp = spellCondition(postDef, af, exp);
		this.exp = spellPre_exp;
	}


	@Override
	public boolean isStateful() {
		return true;
	}
	
	@Override
	public String getContext() {
		return null;
	}

	@Override
	public PExp getContextNode(PExp stitch) {
		return AstExpressionFactory.newAImpliesBooleanBinaryExp(exp.clone(),
				stitch);
	}

	private PExp spellCondition(AExplicitFunctionDefinition def,
			IPogAssistantFactory af, AApplyExp exp) {
		List<Substitution> subs = new LinkedList<Substitution>();

		for (int i = 0; i < exp.getArgs().size(); i++) {
			PPattern orig = def.getParamPatternList().get(i).get(0);
			ILexNameToken origName = af.createPPatternAssistant()
					.getAllVariableNames(orig).get(0);
			PExp new_exp = exp.getArgs().get(0);
			subs.add(new Substitution(origName, new_exp));
		}
		return rewriteExp(def, subs);
	}

	private PExp spellCondition(AExplicitFunctionDefinition def,
			IPogAssistantFactory af, ACallStm stm) {
		List<Substitution> subs = new LinkedList<Substitution>();

		for (int i = 0; i < stm.getArgs().size(); i++) {
			PPattern orig = def.getParamPatternList().get(i).get(0);
			ILexNameToken origName = af.createPPatternAssistant()
					.getAllVariableNames(orig).get(0);
			PExp new_exp = stm.getArgs().get(0);
			subs.add(new Substitution(origName, new_exp));
		}
		return rewriteExp(def, subs);
	}

	// FIXME unify expression rewrite method with the one from OperationCallObligation
	private PExp rewriteExp(AExplicitFunctionDefinition def,
			List<Substitution> subs) {
		PExp pre_exp = def.getBody().clone();

		for (Substitution sub : subs) {
			try {
				pre_exp = pre_exp.apply(new VariableSubVisitor(), sub);
			} catch (AnalysisException e) {
			
				e.printStackTrace();
			}
		}

		return pre_exp;
	}

}
