package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
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
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionSet;

public class PPatternAssistantTC extends PPatternAssistant
{
	protected static ITypeCheckerAssistantFactory af;
	
	@SuppressWarnings("static-access")
	public PPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	/**
	 * Get a set of definitions for the pattern's variables. Note that if the pattern includes duplicate variable names,
	 * these are collapse into one.
	 */
	public static List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope)
			{
		PDefinitionSet set = af.createPDefinitionSet();
		set.addAll(getAllDefinitions(rp, ptype, scope));
		List<PDefinition> result = new Vector<PDefinition>(set);
		return result;
			}

	/**
	 * Get a complete list of all definitions, including duplicates. This method should only be used only by PP
	 */
	private static List<PDefinition> getAllDefinitions(PPattern pattern,
			PType ptype, NameScope scope)
			{
		if (pattern instanceof AIdentifierPattern) {
			return AIdentifierPatternAssistantTC.getAllDefinitions((AIdentifierPattern) pattern, ptype, scope);
		} else if (pattern instanceof ABooleanPattern
				|| pattern instanceof ACharacterPattern
				|| pattern instanceof AExpressionPattern
				|| pattern instanceof AIgnorePattern
				|| pattern instanceof AIntegerPattern
				|| pattern instanceof ANilPattern
				|| pattern instanceof AQuotePattern
				|| pattern instanceof ARealPattern
				|| pattern instanceof AStringPattern) {
			return new Vector<PDefinition>();
		} else if (pattern instanceof AConcatenationPattern) {
			return AConcatenationPatternAssistantTC.getAllDefinitions((AConcatenationPattern) pattern, ptype, scope);
		} else if (pattern instanceof ARecordPattern) {
			return ARecordPatternAssistantTC.getAllDefinitions((ARecordPattern) pattern, ptype, scope);
		} else if (pattern instanceof ASeqPattern) {
			return ASeqPatternAssistantTC.getAllDefinitions((ASeqPattern) pattern, ptype, scope);
		} else if (pattern instanceof ASetPattern) {
			return ASetPatternAssistantTC.getAllDefinitions((ASetPattern) pattern, ptype, scope);
		} else if (pattern instanceof ATuplePattern) {
			return ATuplePatternAssistantTC.getAllDefinitions((ATuplePattern) pattern, ptype, scope);
		} else if (pattern instanceof AUnionPattern) {
			return AUnionPatternAssistantTC.getAllDefinitions((AUnionPattern) pattern, ptype, scope);
		} else if (pattern instanceof AMapUnionPattern) {
			return AMapUnionPatternAssistantTC.getAllDefinitions((AMapUnionPattern) pattern, ptype, scope);
		} else if (pattern instanceof AMapPattern) {
			return AMapPatternAssistantTC.getAllDefinitions((AMapPattern) pattern, ptype, scope);
		} else {
			assert false : "PPatternAssistant.getDefinitions - should not hit this case";
		return null;
		}

			}

	public static void typeResolve(PPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
			{
		if (pattern instanceof AConcatenationPattern) {
			AConcatenationPatternAssistantTC.typeResolve((AConcatenationPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof AExpressionPattern) {
			AExpressionPatternAssistantTC.typeResolve((AExpressionPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof ARecordPattern) {
			ARecordPatternAssistantTC.typeResolve((ARecordPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof ASeqPattern) {
			ASeqPatternAssistantTC.typeResolve((ASeqPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof ASetPattern) {
			ASetPatternAssistantTC.typeResolve((ASetPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof ATuplePattern) {
			ATuplePatternAssistantTC.typeResolve((ATuplePattern) pattern, rootVisitor, question);
		} else if (pattern instanceof AUnionPattern) {
			AUnionPatternAssistantTC.typeResolve((AUnionPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof AMapPattern) {
			AMapPatternAssistantTC.typeResolve((AMapPattern) pattern, rootVisitor, question);
		} else if (pattern instanceof AMapUnionPattern) {
			AMapUnionPatternAssistantTC.typeResolve((AMapUnionPattern) pattern, rootVisitor, question);
		} else {
			pattern.setResolved(true);
		}
			}

	public static void unResolve(PPattern pattern)
	{
		if (pattern instanceof AConcatenationPattern) {
			AConcatenationPatternAssistantTC.unResolve((AConcatenationPattern) pattern);
		} else if (pattern instanceof ARecordPattern) {
			ARecordPatternAssistantTC.unResolve((ARecordPattern) pattern);
		} else if (pattern instanceof ASeqPattern) {
			ASeqPatternAssistantTC.unResolve((ASeqPattern) pattern);
		} else if (pattern instanceof ASetPattern) {
			ASetPatternAssistantTC.unResolve((ASetPattern) pattern);
		} else if (pattern instanceof ATuplePattern) {
			ATuplePatternAssistantTC.unResolve((ATuplePattern) pattern);
		} else if (pattern instanceof AUnionPattern) {
			AUnionPatternAssistantTC.unResolve((AUnionPattern) pattern);
		} else if (pattern instanceof AMapUnionPattern) {
			AMapUnionPatternAssistantTC.unResolve((AMapUnionPattern) pattern);
		} else if (pattern instanceof AMapPattern) {
			AMapPatternAssistantTC.unResolve((AMapPattern) pattern);
			pattern.setResolved(false);
		} else {
			pattern.setResolved(false);
		}
	}

	public static PType getPossibleType(PPattern pattern)
	{
		if (pattern instanceof ABooleanPattern) {
			return ABooleanPatternAssistantTC.getPossibleType((ABooleanPattern) pattern);
		} else if (pattern instanceof ACharacterPattern) {
			return ACharacterPatternAssistantTC.getPossibleType((ACharacterPattern) pattern);
		} else if (pattern instanceof AConcatenationPattern) {
			return AConcatenationPatternAssistantTC.getPossibleType((AConcatenationPattern) pattern);
		} else if (pattern instanceof AExpressionPattern) {
			return AExpressionPatternAssistantTC.getPossibleTypes((AExpressionPattern) pattern);
		} else if (pattern instanceof AIdentifierPattern) {
			return AIdentifierPatternAssistantTC.getPossibleTypes((AIdentifierPattern) pattern);
		} else if (pattern instanceof AIgnorePattern) {
			return AIgnorePatternAssistantTC.getPossibleTypes((AIgnorePattern) pattern);
		} else if (pattern instanceof AIntegerPattern) {
			return AIntegerPatternAssistantTC.getPossibleTypes((AIntegerPattern) pattern);
		} else if (pattern instanceof ANilPattern) {
			return ANilPatternAssistantTC.getPossibleTypes((ANilPattern) pattern);
		} else if (pattern instanceof AQuotePattern) {
			return AQuotePatternAssistantTC.getPossibleTypes((AQuotePattern) pattern);
		} else if (pattern instanceof ARealPattern) {
			return ARealPatternAssistantTC.getPossibleTypes((ARealPattern) pattern);
		} else if (pattern instanceof ARecordPattern) {
			return ARecordPatternAssistantTC.getPossibleTypes((ARecordPattern) pattern);
		} else if (pattern instanceof ASetPattern) {
			return ASetPatternAssistantTC.getPossibleTypes((ASetPattern) pattern);
		} else if (pattern instanceof ASeqPattern) {
			return ASeqPatternAssistantTC.getPossibleTypes((ASeqPattern) pattern);
		} else if (pattern instanceof AStringPattern) {
			return AStringPatternAssistantTC.getPossibleTypes((AStringPattern) pattern);
		} else if (pattern instanceof ATuplePattern) {
			return ATuplePatternAssistantTC.getPossibleTypes((ATuplePattern) pattern);
		} else if (pattern instanceof AUnionPattern) {
			return AUnionPatternAssistantTC.getPossibleTypes((AUnionPattern) pattern);
		} else {
			assert false : "Should not happen";
		}
		return null;
	}

	public static boolean matches(PPattern pattern, PType expType)
	{
		return TypeComparator.compatible(getPossibleType(pattern), expType);
	}

	public static PExp getMatchingExpression(PPattern pattern)
	{
		if (pattern instanceof ABooleanPattern) {
			return ABooleanPatternAssistantTC.getMatchingExpression((ABooleanPattern) pattern);
		} else if (pattern instanceof ACharacterPattern) {
			return ACharacterPatternAssistantTC.getMatchingExpression((ACharacterPattern) pattern);
		} else if (pattern instanceof AConcatenationPattern) {
			return AConcatenationPatternAssistantTC.getMatchingExpression((AConcatenationPattern) pattern);
		} else if (pattern instanceof AExpressionPattern) {
			return AExpressionPatternAssistantTC.getMatchingExpression((AExpressionPattern) pattern);
		} else if (pattern instanceof AIdentifierPattern) {
			return AIdentifierPatternAssistantTC.getMatchingExpression((AIdentifierPattern) pattern);
		} else if (pattern instanceof AIgnorePattern) {
			return AIgnorePatternAssistantTC.getMatchingExpression((AIgnorePattern) pattern);
		} else if (pattern instanceof AIntegerPattern) {
			return AIntegerPatternAssistantTC.getMatchingExpression((AIntegerPattern) pattern);
		} else if (pattern instanceof ANilPattern) {
			return ANilPatternAssistantTC.getMatchingExpression((ANilPattern) pattern);
		} else if (pattern instanceof AQuotePattern) {
			return AQuotePatternAssistantTC.getMatchingExpression((AQuotePattern) pattern);
		} else if (pattern instanceof ARealPattern) {
			return ARealPatternAssistantTC.getMatchingExpression((ARealPattern) pattern);
		} else if (pattern instanceof ARecordPattern) {
			return ARecordPatternAssistantTC.getMatchingExpression((ARecordPattern) pattern);
		} else if (pattern instanceof ASeqPattern) {
			return ASeqPatternAssistantTC.getMatchingExpression((ASeqPattern) pattern);
		} else if (pattern instanceof ASetPattern) {
			return ASetPatternAssistantTC.getMatchingExpression((ASetPattern) pattern);
		} else if (pattern instanceof AStringPattern) {
			return AStringPatternAssistantTC.getMatchingExpression((AStringPattern) pattern);
		} else if (pattern instanceof ATuplePattern) {
			return ATuplePatternAssistantTC.getMatchingExpression((ATuplePattern) pattern);
		} else if (pattern instanceof AUnionPattern) {
			return AUnionPatternAssistantTC.getMatchingExpression((AUnionPattern) pattern);
		} else {
			assert false : "Should not happen";
		return null;
		}
	}

	public static boolean isSimple(PPattern pattern)
	{
		if (pattern instanceof AConcatenationPattern) {
			return AConcatenationPatternAssistantTC.isSimple((AConcatenationPattern) pattern);
		} else if (pattern instanceof AIdentifierPattern) {
			return AIdentifierPatternAssistantTC.isSimple((AIdentifierPattern) pattern);
		} else if (pattern instanceof AIgnorePattern) {
			return AIgnorePatternAssistantTC.isSimple((AIgnorePattern) pattern);
		} else if (pattern instanceof AMapUnionPattern) {
			return AMapUnionPatternAssistantTC.isSimple((AMapUnionPattern) pattern);
		} else if (pattern instanceof ARecordPattern) {
			return ARecordPatternAssistantTC.isSimple((ARecordPattern) pattern);
		} else if (pattern instanceof ASeqPattern) {
			return ASeqPatternAssistantTC.isSimple((ASeqPattern) pattern);
		} else if (pattern instanceof ASetPattern) {
			return ASetPatternAssistantTC.isSimple((ASetPattern) pattern);
		} else if (pattern instanceof ATuplePattern) {
			return ATuplePatternAssistantTC.isSimple((ATuplePattern) pattern);
		} else if (pattern instanceof AUnionPattern) {
			return AUnionPatternAssistantTC.isSimple((AUnionPattern) pattern);
		} else if (pattern instanceof AMapPattern) {
			return AMapPatternAssistantTC.isSimple((AMapPattern) pattern);
		} else {
			/*
			 * True if the pattern is a simple value that can match only one value for certain. Most pattern types
			 * are like this, but any that include variables or ignore patterns are not.
			 */
			return true;
		}
	}
	
	public static boolean alwaysMatches(PPattern pattern)
	{
		if (pattern instanceof ARecordPattern)
			return PPatternListAssistantTC.alwaysMatches(((ARecordPattern) pattern).getPlist());
		else if (pattern instanceof AIgnorePattern)
			return true;
		else if (pattern instanceof AIdentifierPattern)
			return true;
		else
		{
			return false;
		}
	}

}
