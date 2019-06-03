package org.overture.interpreter.utilities.pattern;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ConstrainedPatternChecker extends AnswerAdaptor<Boolean>
{
	protected IInterpreterAssistantFactory af;

	public ConstrainedPatternChecker(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return pattern.getConstrained(); // The variable may be constrained to be the same as another occurrence
	}

	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAMapPattern(AMapPattern pattern)
			throws AnalysisException
	{
		for (AMapletPatternMaplet p : pattern.getMaplets())
		{
			if (p.apply(THIS))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return isConstrained(pattern.getPlist());

	}

	@Override
	public Boolean caseASeqPattern(ASeqPattern pattern)
			throws AnalysisException
	{
		return isConstrained(pattern.getPlist());
	}

	@Override
	public Boolean caseASetPattern(ASetPattern pattern)
			throws AnalysisException
	{
		if (af.createPTypeAssistant().isUnion(af.createPPatternListAssistant().getPossibleType(pattern.getPlist(), pattern.getLocation()), pattern.getLocation().getModule()))
		{
			return true; // Set types are various, so we must permute
		}

		// Check that lengths of the members are the same
		int length = 0;
		
		for (PPattern p: pattern.getPlist())
		{
			if (length == 0)
			{
				length = p.apply(af.getLengthFinder());
			}
			else
			{
				if (p.apply(af.getLengthFinder()) != length)
				{
					return true;	// Patterns are different sizes, so permute them
				}
			}
		}

		return isConstrained(pattern.getPlist());
	}

	@Override
	public Boolean caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		return isConstrained(pattern.getPlist());
	}

	@Override
	public Boolean caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseAObjectPattern(AObjectPattern pattern)
			throws AnalysisException
	{
		LinkedList<PPattern> list = new LinkedList<PPattern>();
		
		for (ANamePatternPair npp: pattern.getFields())
		{
			list.add(npp.getPattern());
		}
		
		return isConstrained(list);
	}

	@Override
	public Boolean defaultPPattern(PPattern node) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean isConstrained(LinkedList<PPattern> plist)
	{
		for (PPattern p : plist)
		{
			if (af.createPPatternAssistant(null).isConstrained(p))
			{
				return true; // NB. OR
			}
		}

		return false;
	}

}
