package org.overture.pog.obligation;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import java.util.List;
import java.util.Vector;

/**
 * Created by ldc on 08/06/17.
 */
public class OrderedObligation extends ProofObligation
{
	public OrderedObligation(SBinaryExp node, List<PType> types, IPOContextStack ctxt,IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.ORDERED,ctxt,node.getLocation(),af);

		PExp lExp = node.getLeft();
		PExp rExp = node.getRight();

		PExp r = stitchIsExps(types,lExp,rExp);
		valuetree.setPredicate(ctxt.getPredWithContext(r));
	}

	private PExp stitchIsExps(List<PType> types, PExp lExp, PExp rExp) {
		if (types.size() == 1){
			return makeIsType(lExp, rExp, types.get(0));
		}
		else {
			return AstExpressionFactory.newAOrBooleanBinaryExp(
					makeIsType(lExp,rExp,types.get(0)),
							stitchIsExps(types.subList(1,types.size()),lExp,rExp));
		}
	}

	private PExp makeIsType(PExp lExp, PExp rExp, PType type)
	{
		PExp lIs = AstExpressionFactory.newAIsExp(type.clone(),lExp.clone());
		PExp rIs = AstExpressionFactory.newAIsExp(type.clone(),rExp.clone());
		PExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(lIs,rIs);
		return andExp;
	}
}
