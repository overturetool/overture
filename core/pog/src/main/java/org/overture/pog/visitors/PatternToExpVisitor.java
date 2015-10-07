package org.overture.pog.visitors;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
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
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
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
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.UniqueNameGenerator;

public class PatternToExpVisitor extends AnswerAdaptor<PExp>
{
	private final UniqueNameGenerator unique;
	private final IPogAssistantFactory af;

	public PatternToExpVisitor(UniqueNameGenerator unique,
			IPogAssistantFactory af)
	{
		this.af = af;
		this.unique = unique;
	}

	public PExp defaultPPattern(PPattern node) throws AnalysisException
	{
		throw new RuntimeException("Cannot convert pattern to Expression: "
				+ node);
	}

	/**
	 * First, literal patterns convert to expressions easily:
	 * 
	 * @param node
	 * @return
	 * @throws AnalysisException
	 */

	public PExp caseABooleanPattern(ABooleanPattern node)
			throws AnalysisException
	{
		ABooleanConstExp b = new ABooleanConstExp();
		b.setValue(node.getValue().clone());
		addPossibleType(b, node);
		return b;
	}

	public PExp caseACharacterPattern(ACharacterPattern node)
			throws AnalysisException
	{
		ACharLiteralExp ch = new ACharLiteralExp();
		ch.setValue(node.getValue().clone());
		addPossibleType(ch, node);
		return ch;
	}

	public PExp caseAStringPattern(AStringPattern node)
			throws AnalysisException
	{
		AStringLiteralExp string = new AStringLiteralExp();
		string.setValue(node.getValue().clone());
		addPossibleType(string, node);
		return string;
	}

	public PExp caseAExpressionPattern(AExpressionPattern node)
			throws AnalysisException
	{
		return node.getExp();
	}

	public PExp caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		AVariableExp var = new AVariableExp();
		var.setName(node.getName().clone());
		var.setOriginal(var.getName().getFullName());
		var.setVardef(pattern2DummyDef(node));

		addPossibleType(var, node);
		return var;
	}

	public PExp caseAIgnorePattern(AIgnorePattern node)
			throws AnalysisException
	{
		AVariableExp var = new AVariableExp();
		var.setName(unique.getUnique("any"));
		var.setOriginal(var.getName().getFullName());
		var.setVardef(pattern2DummyDef(node));
		addPossibleType(var, node);
		return var;
	}

	public PExp caseAIntegerPattern(AIntegerPattern node)
			throws AnalysisException
	{
		AIntLiteralExp exp = new AIntLiteralExp();
		exp.setValue(node.getValue().clone());
		addPossibleType(exp, node);
		return exp;
	}

	public PExp caseANilPattern(ANilPattern node) throws AnalysisException
	{
		return addPossibleType(new ANilExp(), node);
	}

	public PExp caseAQuotePattern(AQuotePattern node) throws AnalysisException
	{
		AQuoteLiteralExp quote = new AQuoteLiteralExp();
		quote.setValue(node.getValue().clone());
		addPossibleType(quote, node);
		return quote;
	}

	public PExp caseARealPattern(ARealPattern node) throws AnalysisException
	{
		ARealLiteralExp exp = new ARealLiteralExp();
		exp.setValue(node.getValue().clone());
		addPossibleType(exp, node);
		return exp;
	}

	/**
	 * Now, compound patterns involve recursive calls to expand their pattern components to expressions.
	 * 
	 * @param node
	 * @return
	 * @throws AnalysisException
	 */

	public PExp caseARecordPattern(ARecordPattern node)
			throws AnalysisException
	{
		AMkTypeExp mkExp = new AMkTypeExp();
		mkExp.setTypeName(node.getTypename().clone());
		List<PExp> args = new Vector<PExp>();

		for (PPattern p : node.getPlist())
		{
			args.add(p.apply(this).clone());
		}
		addPossibleType(mkExp, node);
		mkExp.setArgs(args);
		return mkExp;
	}

	public PExp caseATuplePattern(ATuplePattern node) throws AnalysisException
	{
		ATupleExp tuple = new ATupleExp();
		List<PExp> values = new Vector<PExp>();

		for (PPattern p : node.getPlist())
		{
			PExp e = p.apply(this);
			values.add(e.clone());
		}

		addPossibleType(tuple, node);
		tuple.setArgs(values);
		return tuple;
	}

	public PExp caseASeqPattern(ASeqPattern node) throws AnalysisException
	{
		ASeqEnumSeqExp seq = new ASeqEnumSeqExp();
		List<PExp> values = new Vector<PExp>();

		for (PPattern p : node.getPlist())
		{
			values.add(p.apply(this).clone());
		}

		seq.setMembers(values);
		return addPossibleType(seq, node);
	}

	public PExp caseAConcatenationPattern(AConcatenationPattern node)
			throws AnalysisException
	{
		ASeqConcatBinaryExp conc = new ASeqConcatBinaryExp();
		conc.setLeft(node.getLeft().apply(this).clone());
		conc.setOp(new LexKeywordToken(VDMToken.CONCATENATE, null));
		conc.setRight(node.getRight().apply(this).clone());
		return addPossibleType(conc, node);
	}

	public PExp caseASetPattern(ASetPattern node) throws AnalysisException
	{
		ASetEnumSetExp set = new ASetEnumSetExp();
		List<PExp> values = new Vector<PExp>();

		for (PPattern p : node.getPlist())
		{
			values.add(p.apply(this).clone());
		}

		set.setMembers(values);
		return addPossibleType(set, node);
	}

	public PExp caseAUnionPattern(AUnionPattern node) throws AnalysisException
	{
		ASetUnionBinaryExp union = new ASetUnionBinaryExp();
		union.setLeft(node.getLeft().apply(this).clone());
		union.setOp(new LexKeywordToken(VDMToken.UNION, null));
		union.setRight(node.getRight().apply(this).clone());
		return addPossibleType(union, node);
	}

	public PExp caseAMapPattern(AMapPattern node) throws AnalysisException
	{
		AMapEnumMapExp map = new AMapEnumMapExp();
		List<AMapletExp> values = new Vector<AMapletExp>();

		for (AMapletPatternMaplet p : node.getMaplets())
		{
			values.add((AMapletExp) p.apply(this).clone());
		}

		map.setMembers(values);
		return addPossibleType(map, node);
	}

	public PExp caseAMapletPatternMaplet(AMapletPatternMaplet node)
			throws AnalysisException
	{
		AMapletExp maplet = new AMapletExp();
		PExp from = node.getFrom().apply(this).clone();
		maplet.setLeft(from);

		PExp to = node.getTo().apply(this).clone();
		maplet.setRight(to);
		maplet.setType(AstFactory.newAMapMapType(null, from.getType().clone(), to.getType().clone()));

		return maplet;
	}

	public PExp caseAMapUnionPattern(AMapUnionPattern node)
			throws AnalysisException
	{
		AMapUnionBinaryExp union = new AMapUnionBinaryExp();
		union.setLeft(node.getLeft().apply(this).clone());
		union.setOp(new LexKeywordToken(VDMToken.MUNION, null));
		union.setRight(node.getRight().apply(this).clone());
		return addPossibleType(union, node);
	}

	@Override
	public PExp createNewReturnValue(INode node)
	{

		throw new RuntimeException("Cannot convert pattern to Expression: "
				+ node);

	}

	@Override
	public PExp createNewReturnValue(Object node)
	{
		throw new RuntimeException("Cannot convert pattern to Expression: "
				+ node);
	}

	/*
	 * VarExps in the CGP need a corresponding vardef or it crashes. So we add dummy definitions to avoid the crash. The
	 * definition is never needed for anything.
	 */
	private ALocalDefinition pattern2DummyDef(PPattern pat)
	{
		ALocalDefinition r = AstFactory.newALocalDefinition(null, new LexNameToken("", "", pat.getLocation().clone()), NameScope.LOCAL, new AUnknownType());
		return r;
	}

	private PExp addPossibleType(PExp exp, PPattern node)
	{
		PType possibleType = af.createPPatternAssistant().getPossibleType(node);
		if (possibleType != null)
		{
			exp.setType(possibleType.clone());
		} else
		{
			exp.setType(new AUnknownType());
		}
		return exp;
	}
}
