package org.overture.pog.obligation;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.POType;

import java.util.List;

/**
 * Created by ldc on 08/06/17.
 */
public class OrderedObligation extends ProofObligation
{
	public OrderedObligation(PExp node, List<PType> types, IPOContextStack ctxt,IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.ORDERED,ctxt,node.getLocation(),af);

		AVariableExp nyexp = getVarExp(new LexNameToken("", "Ordered", null));
		valuetree.setPredicate(nyexp);
	}
}
