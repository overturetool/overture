package org.overture.pog.utility;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;

public class PatternAlwaysMatchesVisitor extends AnswerAdaptor<Boolean>
{
	public PatternAlwaysMatchesVisitor()
	{
	}

	private Boolean alwaysMatches(List<PPattern> plist)
			throws AnalysisException
	{
		for (PPattern p : plist)
		{
			if (!p.apply(this))
			{
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	@Override
	public Boolean defaultPPattern(PPattern node) throws AnalysisException
	{
		return Boolean.FALSE; // Most patterns do not always match
	}

	/**
	 * First, literal patterns always match:
	 * 
	 * @param node
	 * @return
	 * @throws AnalysisException
	 */

	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		return Boolean.TRUE;
	}

	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern node)
			throws AnalysisException
	{
		return Boolean.TRUE;
	}

	/**
	 * Now, a couple of patterns involve recursive calls to AND their components.
	 * 
	 * @param node
	 * @return
	 * @throws AnalysisException
	 */

	@Override
	public Boolean caseARecordPattern(ARecordPattern node)
			throws AnalysisException
	{
		return alwaysMatches(node.getPlist());
	}

	@Override
	public Boolean caseATuplePattern(ATuplePattern node)
			throws AnalysisException
	{
		return alwaysMatches(node.getPlist());
	}

	@Override
	public Boolean caseAObjectPattern(AObjectPattern node)
			throws AnalysisException
	{
		LinkedList<PPattern> list = new LinkedList<PPattern>();
		
		for (ANamePatternPair npp: node.getFields())
		{
			list.add(npp.getPattern());
		}
		
		return alwaysMatches(list);
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "Should not happen";
		return null;
	}
}
