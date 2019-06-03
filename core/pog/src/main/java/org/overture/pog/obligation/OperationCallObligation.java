package org.overture.pog.obligation;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SOperationDefinitionBase;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.pog.utility.Substitution;

public class OperationCallObligation extends ProofObligation
{

	public OperationCallObligation(ACallStm stm, SOperationDefinitionBase def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(stm, POType.OP_CALL, ctxt, stm.getLocation(), af);

		// cannot quote pre-cond so we spell it out with rewritten arguments
		List<Substitution> subs = new LinkedList<Substitution>();

		for (int i = 0; i < stm.getArgs().size(); i++)
		{
			PPattern orig = def.getPredef().getParamPatternList().get(0).get(i);
			ILexNameToken origName = af.createPPatternAssistant(def.getLocation().getModule()).getAllVariableNames(orig).get(0).clone();
			PExp new_exp = stm.getArgs().get(0);
			subs.add(new Substitution(origName, new_exp));

		}
		PExp pre_exp = def.getPrecondition().clone();

		for (Substitution sub : subs)
		{
			pre_exp = pre_exp.clone().apply(af.getVarSubVisitor(), sub);

		}

		stitch = pre_exp;
		valuetree.setPredicate(ctxt.getPredWithContext(pre_exp));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}