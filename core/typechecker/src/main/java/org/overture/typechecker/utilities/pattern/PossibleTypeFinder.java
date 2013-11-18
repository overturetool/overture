package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.ABooleanPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ACharacterPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AConcatenationPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AExpressionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIdentifierPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIgnorePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIntegerPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ANilPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AQuotePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARealPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARecordPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASeqPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AStringPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AUnionPatternAssistantTC;

/**
 * Used to get if a possible type out of a pattern.
 *  
 * @author kel
 */
public class PossibleTypeFinder extends AnswerAdaptor<PType>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public PossibleTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public PType caseABooleanPattern(ABooleanPattern pattern)
			throws AnalysisException
	{
		return ABooleanPatternAssistantTC.getPossibleType(pattern);
	}
	
	@Override
	public PType caseACharacterPattern(ACharacterPattern pattern)
			throws AnalysisException
	{
		return ACharacterPatternAssistantTC.getPossibleType(pattern);
	}
	
	@Override
	public PType caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		return AConcatenationPatternAssistantTC.getPossibleType(pattern);
	}
	
	@Override
	public PType caseAExpressionPattern(AExpressionPattern pattern)
			throws AnalysisException
	{
		return AExpressionPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return AIdentifierPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return AIgnorePatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseAIntegerPattern(AIntegerPattern pattern)
			throws AnalysisException
	{
		return AIntegerPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseANilPattern(ANilPattern pattern) throws AnalysisException
	{
		return ANilPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseAQuotePattern(AQuotePattern pattern) throws AnalysisException
	{
		return AQuotePatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseARealPattern(ARealPattern pattern) throws AnalysisException
	{
		return ARealPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return ARecordPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		return ASetPatternAssistantTC.getPossibleTypes(pattern);
	}
	@Override
	public PType caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return ASeqPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseAStringPattern(AStringPattern pattern)
			throws AnalysisException
	{
		return AStringPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType caseATuplePattern(ATuplePattern pattern) throws AnalysisException
	{
		return ATuplePatternAssistantTC.getPossibleTypes(pattern);
	}

	@Override
	public PType caseAUnionPattern(AUnionPattern pattern) throws AnalysisException
	{
		return AUnionPatternAssistantTC.getPossibleTypes(pattern);
	}
	
	@Override
	public PType defaultPPattern(PPattern pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

}
