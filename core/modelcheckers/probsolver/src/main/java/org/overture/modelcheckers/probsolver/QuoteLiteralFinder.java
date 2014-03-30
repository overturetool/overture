package org.overture.modelcheckers.probsolver;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.types.AQuoteType;

/**
 * Utility class capable of extracting all quote names from a node tree
 * 
 * @author kel
 */
public class QuoteLiteralFinder extends DepthFirstAnalysisAdaptor
{
	final Set<String> quotes = new HashSet<String>();

	@Override
	public void caseAQuoteLiteralExp(AQuoteLiteralExp node)
			throws AnalysisException
	{
		quotes.add(node.getValue().getValue());
	}

	@Override
	public void caseAQuoteType(AQuoteType node) throws AnalysisException
	{
		quotes.add(node.getValue().getValue());
	}

	public Set<String> getQuoteLiterals()
	{
		return this.quotes;
	}
}
