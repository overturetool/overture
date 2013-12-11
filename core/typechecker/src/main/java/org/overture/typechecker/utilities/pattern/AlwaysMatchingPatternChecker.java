package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

/**
 * Used to check if a given pattern always matches with its type.
 * 
 * @author kel
 */
public class AlwaysMatchingPatternChecker extends AnswerAdaptor<Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public AlwaysMatchingPatternChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return PPatternListAssistantTC.alwaysMatches(pattern.getPlist());
	}

	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		return PPatternListAssistantTC.alwaysMatches(pattern.getPlist());
	}

	@Override
	public Boolean defaultPPattern(PPattern pattern) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{

		return false;
	}

}
