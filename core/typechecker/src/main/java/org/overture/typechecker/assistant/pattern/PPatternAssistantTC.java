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
import org.overture.typechecker.assistant.definition.PDefinitionSet;

public class PPatternAssistantTC extends PPatternAssistant
{

	/**
	 * Get a set of definitions for the pattern's variables. Note that if the
	 * pattern includes duplicate variable names, these are collapse into one.
	 */
	public static List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope)
	{
		PDefinitionSet set = new PDefinitionSet();
		set.addAll(getAllDefinitions(rp,ptype, scope));
		List<PDefinition> result = new Vector<PDefinition>(set);
		return result;
	}

	
	
	/**
	 * Get a complete list of all definitions, including duplicates. This method should only be used 
	 * only by PP
	 */
	private static List<PDefinition> getAllDefinitions(PPattern rp, PType ptype,
			NameScope scope)
	{
		switch (rp.kindPPattern())
		{
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantTC.getAllDefinitions((AIdentifierPattern) rp,ptype,scope);
			case ABooleanPattern.kindPPattern:
			case ACharacterPattern.kindPPattern:
			case AExpressionPattern.kindPPattern:
			case AIgnorePattern.kindPPattern:
			case AIntegerPattern.kindPPattern:
			case ANilPattern.kindPPattern:
			case AQuotePattern.kindPPattern:
			case ARealPattern.kindPPattern:
			case AStringPattern.kindPPattern:
				return new Vector<PDefinition>();
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantTC.getAllDefinitions((AConcatenationPattern) rp, ptype, scope);
			case ARecordPattern.kindPPattern:
				return ARecordPatternAssistantTC.getAllDefinitions((ARecordPattern) rp, ptype, scope);
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantTC.getAllDefinitions((ASeqPattern) rp, ptype, scope);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantTC.getAllDefinitions((ASetPattern) rp, ptype, scope);
			case ATuplePattern.kindPPattern:
				return ATuplePatternAssistantTC.getAllDefinitions((ATuplePattern) rp, ptype, scope);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantTC.getAllDefinitions((AUnionPattern) rp, ptype, scope);
			case AMapUnionPattern.kindPPattern:
				return AMapUnionPatternAssistantTC.getAllDefinitions((AMapUnionPattern)rp,ptype,scope);
			case AMapPattern.kindPPattern:
				return AMapPatternAssistantTC.getAllDefinitions((AMapPattern)rp,ptype,scope);
			default:
				assert false : "PPatternAssistant.getDefinitions - should not hit this case";
				return null;
		}

	}

	public static void typeResolve(PPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		switch (pattern.kindPPattern())
		{
			case AConcatenationPattern.kindPPattern:
				AConcatenationPatternAssistantTC.typeResolve((AConcatenationPattern) pattern, rootVisitor, question);
				break;
			case AExpressionPattern.kindPPattern:
				AExpressionPatternAssistantTC.typeResolve((AExpressionPattern) pattern, rootVisitor, question);
				break;
			case ARecordPattern.kindPPattern:
				ARecordPatternAssistantTC.typeResolve((ARecordPattern) pattern, rootVisitor, question);
				break;
			case ASeqPattern.kindPPattern:
				ASeqPatternAssistantTC.typeResolve((ASeqPattern) pattern, rootVisitor, question);
				break;
			case ASetPattern.kindPPattern:
				ASetPatternAssistantTC.typeResolve((ASetPattern) pattern, rootVisitor, question);
				break;
			case ATuplePattern.kindPPattern:
				ATuplePatternAssistantTC.typeResolve((ATuplePattern) pattern, rootVisitor, question);
				break;
			case AUnionPattern.kindPPattern:
				AUnionPatternAssistantTC.typeResolve((AUnionPattern) pattern, rootVisitor, question);
				break;
			case AMapPattern.kindPPattern:
				AMapPatternAssistantTC.typeResolve((AMapPattern)pattern,rootVisitor, question);
				break;
			case AMapUnionPattern.kindPPattern:
				AMapUnionPatternAssistantTC.typeResolve((AMapUnionPattern) pattern,rootVisitor, question);
				break;
			default:
				pattern.setResolved(true);
				break;
		}
	}

	public static void unResolve(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case AConcatenationPattern.kindPPattern:
				AConcatenationPatternAssistantTC.unResolve((AConcatenationPattern) pattern);
				break;
			case ARecordPattern.kindPPattern:
				ARecordPatternAssistantTC.unResolve((ARecordPattern) pattern);
				break;
			case ASeqPattern.kindPPattern:
				ASeqPatternAssistantTC.unResolve((ASeqPattern) pattern);
				break;
			case ASetPattern.kindPPattern:
				ASetPatternAssistantTC.unResolve((ASetPattern) pattern);
				break;
			case ATuplePattern.kindPPattern:
				ATuplePatternAssistantTC.unResolve((ATuplePattern) pattern);
				break;
			case AUnionPattern.kindPPattern:
				AUnionPatternAssistantTC.unResolve((AUnionPattern) pattern);
				break;
			case AMapUnionPattern.kindPPattern:
				AMapUnionPatternAssistantTC.unResolve((AMapUnionPattern) pattern);
				break;
			case AMapPattern.kindPPattern:
				AMapPatternAssistantTC.unResolve((AMapPattern) pattern);
			default:
				pattern.setResolved(false);
				break;
		}
	}

	public static PType getPossibleType(PPattern pattern)
	{
		switch (pattern.kindPPattern())
		{
			case ABooleanPattern.kindPPattern:
				return ABooleanPatternAssistantTC.getPossibleType((ABooleanPattern) pattern);				
			case ACharacterPattern.kindPPattern:
				return ACharacterPatternAssistantTC.getPossibleType((ACharacterPattern) pattern);	
			case AConcatenationPattern.kindPPattern:
				return AConcatenationPatternAssistantTC.getPossibleType((AConcatenationPattern) pattern);			
			case AExpressionPattern.kindPPattern:
				return AExpressionPatternAssistantTC.getPossibleTypes((AExpressionPattern) pattern);
			case AIdentifierPattern.kindPPattern:
				return AIdentifierPatternAssistantTC.getPossibleTypes((AIdentifierPattern) pattern);
			case AIgnorePattern.kindPPattern:
				return AIgnorePatternAssistantTC.getPossibleTypes((AIgnorePattern) pattern);
			case AIntegerPattern.kindPPattern:
				return AIntegerPatternAssistantTC.getPossibleTypes((AIntegerPattern) pattern);
			case ANilPattern.kindPPattern:
				return ANilPatternAssistantTC.getPossibleTypes((ANilPattern) pattern);
			case AQuotePattern.kindPPattern:
				return AQuotePatternAssistantTC.getPossibleTypes((AQuotePattern) pattern);
			case ARealPattern.kindPPattern:
				return ARealPatternAssistantTC.getPossibleTypes((ARealPattern) pattern);
			case ARecordPattern.kindPPattern:
				return ARecordPatternAssistantTC.getPossibleTypes((ARecordPattern) pattern);
			case ASetPattern.kindPPattern:
				return ASetPatternAssistantTC.getPossibleTypes((ASetPattern) pattern);			
			case ASeqPattern.kindPPattern:
				return ASeqPatternAssistantTC.getPossibleTypes((ASeqPattern) pattern);
			case AStringPattern.kindPPattern:
				return AStringPatternAssistantTC.getPossibleTypes((AStringPattern) pattern);			
			case ATuplePattern.kindPPattern:
				return ATuplePatternAssistantTC.getPossibleTypes((ATuplePattern) pattern);
			case AUnionPattern.kindPPattern:
				return AUnionPatternAssistantTC.getPossibleTypes((AUnionPattern) pattern);
			default:
				assert false : "Should not happen";
		}
		return null;
	}

	public static boolean matches(PPattern pattern, PType expType)
	{
		return TypeComparator.compatible(getPossibleType(pattern), expType);
	}

	
	
	public static PExp getMatchingExpression(PPattern p) {
		switch (p.kindPPattern()) {
		case ABooleanPattern.kindPPattern:
			return ABooleanPatternAssistantTC.getMatchingExpression((ABooleanPattern) p);
		case ACharacterPattern.kindPPattern:
			return ACharacterPatternAssistantTC.getMatchingExpression((ACharacterPattern)p);
		case AConcatenationPattern.kindPPattern:
			return AConcatenationPatternAssistantTC.getMatchingExpression((AConcatenationPattern)p);
		case AExpressionPattern.kindPPattern:
			return AExpressionPatternAssistantTC.getMatchingExpression((AExpressionPattern) p);
		case AIdentifierPattern.kindPPattern:
			return AIdentifierPatternAssistantTC.getMatchingExpression((AIdentifierPattern) p);
		case AIgnorePattern.kindPPattern:
			return AIgnorePatternAssistantTC.getMatchingExpression((AIgnorePattern) p);
		case AIntegerPattern.kindPPattern:
			return AIntegerPatternAssistantTC.getMatchingExpression((AIntegerPattern) p);
		case ANilPattern.kindPPattern:
			return ANilPatternAssistantTC.getMatchingExpression((ANilPattern) p);
		case AQuotePattern.kindPPattern:
			return AQuotePatternAssistantTC.getMatchingExpression((AQuotePattern) p);
		case ARealPattern.kindPPattern:
			return ARealPatternAssistantTC.getMatchingExpression((ARealPattern) p);
		case ARecordPattern.kindPPattern:
			return ARecordPatternAssistantTC.getMatchingExpression((ARecordPattern) p);
		case ASeqPattern.kindPPattern:
			return ASeqPatternAssistantTC.getMatchingExpression((ASeqPattern) p);
		case ASetPattern.kindPPattern:
			return ASetPatternAssistantTC.getMatchingExpression((ASetPattern) p);
		case AStringPattern.kindPPattern:
			return AStringPatternAssistantTC.getMatchingExpression((AStringPattern) p);
		case ATuplePattern.kindPPattern:
			return ATuplePatternAssistantTC.getMatchingExpression((ATuplePattern) p);
		case AUnionPattern.kindPPattern:
			return AUnionPatternAssistantTC.getMatchingExpression((AUnionPattern) p);
		default:
			assert false : "Should not happen";
			return null;
		}
	}
	
	public static boolean isSimple(PPattern p)
	{
		
		switch (p.kindPPattern()) {
		case AConcatenationPattern.kindPPattern:
			return AConcatenationPatternAssistantTC.isSimple((AConcatenationPattern) p);
		case AIdentifierPattern.kindPPattern:
			return AIdentifierPatternAssistantTC.isSimple((AIdentifierPattern) p);
		case AIgnorePattern.kindPPattern:
			return AIgnorePatternAssistantTC.isSimple((AIgnorePattern) p);
		case AMapUnionPattern.kindPPattern:
			return AMapUnionPatternAssistantTC.isSimple((AMapUnionPattern) p);
		case ARecordPattern.kindPPattern:
			return ARecordPatternAssistantTC.isSimple((ARecordPattern) p);
		case ASeqPattern.kindPPattern:
			return ASeqPatternAssistantTC.isSimple((ASeqPattern) p);
		case ASetPattern.kindPPattern:
			return ASetPatternAssistantTC.isSimple((ASetPattern) p);
		case ATuplePattern.kindPPattern:
			return ATuplePatternAssistantTC.isSimple((ATuplePattern) p);
		case AUnionPattern.kindPPattern:
			return AUnionPatternAssistantTC.isSimple((AUnionPattern) p);
		case AMapPattern.kindPPattern:
			return AMapPatternAssistantTC.isSimple((AMapPattern) p);
		default:
			/*
			 * True if the pattern is a simple value that can match only
			 * one value for certain. Most pattern types are like this, but any
			 * that include variables or ignore patterns are not.
			 */ 
			return true;
		}
	}
	
}
