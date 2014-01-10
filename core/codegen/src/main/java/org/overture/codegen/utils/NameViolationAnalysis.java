package org.overture.codegen.utils;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;

public class NameViolationAnalysis extends ViolationAnalysis
{
	private NamingComparison comparison;

	public NameViolationAnalysis(NamingComparison comparison)
	{
		this.comparison = comparison;
	}

	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if (node instanceof ILexNameToken)
		{
			ILexNameToken nameToken = (ILexNameToken) node;

			if (comparison.isInvalid(nameToken))
			{
				String name = nameToken.getName();
				ILexLocation location = nameToken.getLocation();

				Violation violation = new Violation(name, location);
				addViolation(violation);
			}
		}
	}
}
