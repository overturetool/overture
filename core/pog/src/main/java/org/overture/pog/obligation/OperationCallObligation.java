package org.overture.pog.obligation;

import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACallStm;
import org.overture.pog.pub.IPOContextStack;

public class OperationCallObligation extends ProofObligation {

	public OperationCallObligation(ACallStm stm,
			AExplicitOperationDefinition node, List<PExp> args,
			IPOContextStack ctxt) {
		super(stm, POType.OP_CALL, ctxt, stm.getLocation());

		PExp pre_exp = node.getPrecondition().clone();
		valuetree.setPredicate(ctxt.getPredWithContext(pre_exp));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
