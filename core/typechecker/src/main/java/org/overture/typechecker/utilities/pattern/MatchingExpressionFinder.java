package org.overture.typechecker.utilities.pattern;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexBooleanToken;
import org.overture.ast.intf.lex.ILexCharacterToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexQuoteToken;
import org.overture.ast.intf.lex.ILexRealToken;
import org.overture.ast.intf.lex.ILexStringToken;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexCharacterToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexRealToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
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
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

/**
 * Used to get Matching expressions out of a pattern.
 * 
 * @author kel
 */
public class MatchingExpressionFinder extends AnswerAdaptor<PExp>
{
	protected ITypeCheckerAssistantFactory af;

	public MatchingExpressionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PExp caseABooleanPattern(ABooleanPattern pattern)
			throws AnalysisException
	{
		ILexBooleanToken tok = pattern.getValue();
		ABooleanConstExp res = AstFactory.newABooleanConstExp((LexBooleanToken) tok.clone());
		return res;
	}

	@Override
	public PExp caseACharacterPattern(ACharacterPattern pattern)
			throws AnalysisException
	{
		ILexCharacterToken v = pattern.getValue();
		return AstFactory.newACharLiteralExp((LexCharacterToken) v.clone());
	}

	@Override
	public PExp caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		LexToken op = new LexKeywordToken(VDMToken.CONCATENATE, pattern.getLocation());
		PExp le = af.createPPatternAssistant().getMatchingExpression(pattern.getLeft());
		PExp re = af.createPPatternAssistant().getMatchingExpression(pattern.getRight());
		return AstFactory.newASeqConcatBinaryExp(le, op, re);
	}

	@Override
	public PExp caseAExpressionPattern(AExpressionPattern pattern)
			throws AnalysisException
	{
		return pattern.getExp();
	}

	@Override
	public PExp caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAVariableExp(pattern.getName().clone());
	}

	@Override
	public PExp caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		int var = 1; // This was a private static global variable in the assistant AIgnorePatternAssistantTC.
		// Generate a new "any" name for use during PO generation. The name
		// must be unique for the pattern instance.

		if (pattern.getAnyName() == null)
		{
			pattern.setAnyName(new LexNameToken("", "any" + var++, pattern.getLocation()));
		}

		return AstFactory.newAVariableExp(pattern.getAnyName());
	}

	@Override
	public PExp caseAIntegerPattern(AIntegerPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAIntLiteralExp((LexIntegerToken) pattern.getValue().clone());
	}

	@Override
	public PExp caseANilPattern(ANilPattern pattern) throws AnalysisException
	{
		return AstFactory.newANilExp(pattern.getLocation());
	}

	@Override
	public PExp caseAQuotePattern(AQuotePattern pattern)
			throws AnalysisException
	{
		ILexQuoteToken v = pattern.getValue();
		return AstFactory.newAQuoteLiteralExp(v.clone());
	}

	@Override
	public PExp caseARealPattern(ARealPattern pattern) throws AnalysisException
	{
		ILexRealToken v = pattern.getValue();
		return AstFactory.newARealLiteralExp((LexRealToken) v.clone());
	}

	@Override
	public PExp caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		List<PExp> list = new LinkedList<PExp>();

		for (PPattern p : pattern.getPlist())
		{
			list.add(af.createPPatternAssistant().getMatchingExpression(p));
		}

		ILexNameToken tpName = pattern.getTypename();
		return AstFactory.newAMkTypeExp(tpName.clone(), list);
	}

	@Override
	public PExp caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return AstFactory.newASeqEnumSeqExp(pattern.getLocation(), PPatternListAssistantTC.getMatchingExpressionList(pattern.getPlist()));
	}

	@Override
	public PExp caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		return AstFactory.newASetEnumSetExp(pattern.getLocation(), PPatternListAssistantTC.getMatchingExpressionList(pattern.getPlist()));
	}

	@Override
	public PExp caseAStringPattern(AStringPattern pattern)
			throws AnalysisException
	{
		ILexStringToken v = pattern.getValue();
		return AstFactory.newAStringLiteralExp((ILexStringToken) v.clone());
	}

	@Override
	public PExp caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		return AstFactory.newATupleExp(pattern.getLocation(), PPatternListAssistantTC.getMatchingExpressionList(pattern.getPlist()));
	}

	@Override
	public PExp caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		LexToken op = new LexKeywordToken(VDMToken.UNION, pattern.getLocation());
		return AstFactory.newASetUnionBinaryExp(af.createPPatternAssistant().getMatchingExpression(pattern.getLeft()), op, af.createPPatternAssistant().getMatchingExpression(pattern.getRight()));
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
