package org.overture.modelcheckers.probsolver;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;

public class TokenTypeCalculator extends DepthFirstAnalysisAdaptor
{
	final List<PType> types = new Vector<PType>();

	@Override
	public void caseAMkBasicExp(AMkBasicExp node) throws AnalysisException
	{
		if (node.getType() instanceof ATokenBasicType
				&& node.getArg().getType() != null /*
													 * the pre/post expressions are cloned so the original ones are
													 * never type checked
													 */)
		{
			types.add(node.getArg().getType().clone());
		}
	}

	public PType getTokenType()
	{
		PTypeSet ts = new PTypeSet(types);
		return ts.getType(null);
	}
}
