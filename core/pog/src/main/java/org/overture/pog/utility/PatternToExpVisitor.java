package org.overture.pog.utility;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
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

public class PatternToExpVisitor extends AnswerAdaptor<PExp>
{
	private static final long serialVersionUID = 1L;
	
	private final UniqueNameGenerator unique;
	
	public PatternToExpVisitor(UniqueNameGenerator unique)
	{
		this.unique = unique;
	}

	public PExp defaultPPattern(PPattern node) throws AnalysisException
	{
		throw new RuntimeException("Cannot convert pattern to Expression: " + node);
	}
	
	/**
	 * First, literal patterns convert to expressions easily:
	 */

	public PExp caseABooleanPattern(ABooleanPattern node) throws AnalysisException
	{
		ABooleanConstExp b = new ABooleanConstExp();
		b.setValue(node.getValue());
		return b;
	}

	public PExp caseACharacterPattern(ACharacterPattern node) throws AnalysisException
	{
		ACharLiteralExp ch = new ACharLiteralExp();
		ch.setValue(node.getValue());
		return ch;
	}

	public PExp caseAStringPattern(AStringPattern node) throws AnalysisException
	{
		AStringLiteralExp string = new AStringLiteralExp();
		string.setValue(node.getValue());
		return string;
	}

	public PExp caseAExpressionPattern(AExpressionPattern node) throws AnalysisException
	{
		return node.getExp();
	}

	public PExp caseAIdentifierPattern(AIdentifierPattern node) throws AnalysisException
	{
		AVariableExp var = new AVariableExp();
		var.setName(node.getName().clone());
		var.setOriginal(var.getName().getFullName());
		return var;
	}

	public PExp caseAIgnorePattern(AIgnorePattern node) throws AnalysisException
	{
		AVariableExp var = new AVariableExp();
		var.setName(unique.getUnique("any"));
		var.setOriginal(var.getName().getFullName());
		return var;
	}

	public PExp caseAIntegerPattern(AIntegerPattern node) throws AnalysisException
	{
		AIntLiteralExp exp = new AIntLiteralExp();
		exp.setValue(node.getValue());
		return exp;
	}

	public PExp caseANilPattern(ANilPattern node) throws AnalysisException
	{
		return new ANilExp();
	}

	public PExp caseAQuotePattern(AQuotePattern node) throws AnalysisException
	{
		AQuoteLiteralExp quote = new AQuoteLiteralExp();
		quote.setValue(node.getValue());
		return quote;
	}

	public PExp caseARealPattern(ARealPattern node) throws AnalysisException
	{
		ARealLiteralExp exp = new ARealLiteralExp();
		exp.setValue(node.getValue());
		return exp;
	}

	/**
	 * Now, compound patterns involve recursive calls to expand their
	 * pattern components to expressions.
	 */
	
	public PExp caseARecordPattern(ARecordPattern node) throws AnalysisException
	{
		AMkTypeExp mkExp = new AMkTypeExp();
		mkExp.setTypeName(node.getTypename());
		List<PExp> args = new Vector<PExp>();
		
		for (PPattern p: node.getPlist())
		{
			args.add(p.apply(this));
		}
		
		mkExp.setArgs(args);
		return mkExp;
	}

	public PExp caseATuplePattern(ATuplePattern node) throws AnalysisException
	{
		ATupleExp tuple = new ATupleExp();
		List<PExp> values = new Vector<PExp>();
		
		for (PPattern p: node.getPlist())
		{
			values.add(p.apply(this));
		}
		
		tuple.setArgs(values);
		return tuple;
	}

	public PExp caseASeqPattern(ASeqPattern node) throws AnalysisException
	{
		ASeqEnumSeqExp seq = new ASeqEnumSeqExp();
		List<PExp> values = new Vector<PExp>();
		
		for (PPattern p: node.getPlist())
		{
			values.add(p.apply(this));
		}
		
		seq.setMembers(values);
		return seq;
	}

	public PExp caseAConcatenationPattern(AConcatenationPattern node) throws AnalysisException
	{
		ASeqConcatBinaryExp conc = new ASeqConcatBinaryExp();
		conc.setLeft(node.getLeft().apply(this));
		conc.setOp(new LexKeywordToken(VDMToken.CONCATENATE, null));
		conc.setRight(node.getRight().apply(this));
		return conc;
	}

	public PExp caseASetPattern(ASetPattern node) throws AnalysisException
	{
		ASetEnumSetExp set = new ASetEnumSetExp();
		List<PExp> values = new Vector<PExp>();
		
		for (PPattern p: node.getPlist())
		{
			values.add(p.apply(this));
		}
		
		set.setMembers(values);
		return set;
	}

	public PExp caseAUnionPattern(AUnionPattern node) throws AnalysisException
	{
		ASetUnionBinaryExp union = new ASetUnionBinaryExp();
		union.setLeft(node.getLeft().apply(this));
		union.setOp(new LexKeywordToken(VDMToken.UNION, null));
		union.setRight(node.getRight().apply(this));
		return union;
	}

	public PExp caseAMapPattern(AMapPattern node) throws AnalysisException
	{
		AMapEnumMapExp map = new AMapEnumMapExp();
		List<AMapletExp> values = new Vector<AMapletExp>();
		
		for (AMapletPatternMaplet p: node.getMaplets())
		{
			values.add((AMapletExp) p.apply(this));
		}
		
		map.setMembers(values);
		return map;
	}
	
	public PExp caseAMapletPatternMaplet(AMapletPatternMaplet node) throws AnalysisException
	{
		AMapletExp maplet = new AMapletExp();
		maplet.setLeft(node.getFrom().apply(this));
		maplet.setRight(node.getTo().apply(this));
		return maplet;
	}

	public PExp caseAMapUnionPattern(AMapUnionPattern node) throws AnalysisException
	{
		AMapUnionBinaryExp union = new AMapUnionBinaryExp();
		union.setLeft(node.getLeft().apply(this));
		union.setOp(new LexKeywordToken(VDMToken.MUNION, null));
		union.setRight(node.getRight().apply(this));
		return union;
	}
}
