package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.expressions.PExp;
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
 * Used to get Matching expressions out of a pattern.
 *  
 * @author kel
 */
public class MatchingExpressionFinder extends AnswerAdaptor<PExp>
{
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public MatchingExpressionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public PExp caseABooleanPattern(ABooleanPattern pattern)
			throws AnalysisException
	{
		return ABooleanPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseACharacterPattern(ACharacterPattern pattern)
			throws AnalysisException
	{
		return ACharacterPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		return AConcatenationPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAExpressionPattern(AExpressionPattern pattern)
			throws AnalysisException
	{
		return AExpressionPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return AIdentifierPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return AIgnorePatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAIntegerPattern(AIntegerPattern pattern)
			throws AnalysisException
	{
		return AIntegerPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseANilPattern(ANilPattern pattern) throws AnalysisException
	{
		return ANilPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAQuotePattern(AQuotePattern pattern) throws AnalysisException
	{
		return AQuotePatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseARealPattern(ARealPattern pattern) throws AnalysisException
	{
		return ARealPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return ARecordPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return ASeqPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		return ASetPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAStringPattern(AStringPattern pattern)
			throws AnalysisException
	{
		return AStringPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseATuplePattern(ATuplePattern pattern) throws AnalysisException
	{
		return ATuplePatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp caseAUnionPattern(AUnionPattern pattern) throws AnalysisException
	{
		return AUnionPatternAssistantTC.getMatchingExpression(pattern);
	}
	
	@Override
	public PExp createNewReturnValue(INode pattern) throws AnalysisException
	{
		// TODO Auto-generated method stub
		assert false : "Should not happen";
		return null;
	}

	@Override
	public PExp createNewReturnValue(Object pattern) throws AnalysisException
	{
		// TODO Auto-generated method stub
		assert false : "Should not happen";
		return null;
	}

}
