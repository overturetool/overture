package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AOptionalType;

public abstract class TypeUnwrapper<A> extends AnswerAdaptor<A>
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public A caseABracketType(ABracketType node) throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public A caseAOptionalType(AOptionalType node) throws AnalysisException
	{
		return node.getType().apply(THIS);
	}
}
