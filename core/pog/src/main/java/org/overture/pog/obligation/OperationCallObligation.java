package org.overture.pog.obligation;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.visitors.Substitution;
import org.overture.pog.visitors.VariableSubVisitor;

public class OperationCallObligation extends ProofObligation {

	public OperationCallObligation(ACallStm stm,
			AExplicitOperationDefinition def, List<PExp> args,
			IPOContextStack ctxt, IPogAssistantFactory af) {
		super(stm, POType.OP_CALL, ctxt, stm.getLocation());
		
		// cannot quote pre-cond so we spell it out with rewritten arguments
		List<Substitution> subs = new LinkedList<Substitution>();
		
		for (int i = 0; i < stm.getArgs().size(); i++) {
			PPattern orig = def.getPredef().getParamPatternList().get(i).get(0);
			ILexNameToken origName = af.createPPatternAssistant().getAllVariableNames(orig).get(0);
			PExp new_exp = stm.getArgs().get(0);
			subs.add(new Substitution(origName,new_exp));
			
		}
		PExp pre_exp = def.getPrecondition().clone();
		
		for (Substitution sub : subs){
			try {
				pre_exp = pre_exp.apply(new VariableSubVisitor(), sub);
			} catch (AnalysisException e) {
				// FIXME consider how to deal with analysis exception inside PO constructor
				e.printStackTrace();
			}
		}

		valuetree.setPredicate(ctxt.getPredWithContext(pre_exp));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
