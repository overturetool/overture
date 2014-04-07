package org.overture.modelcheckers.probsolver.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.modelcheckers.probsolver.SolverConsole;

import de.be4.classicalb.core.parser.node.AAddExpression;
import de.be4.classicalb.core.parser.node.ABoolSetExpression;
import de.be4.classicalb.core.parser.node.ABooleanFalseExpression;
import de.be4.classicalb.core.parser.node.ABooleanTrueExpression;
import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.ACompositionExpression;
import de.be4.classicalb.core.parser.node.AComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AConcatExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AConvertBoolExpression;
import de.be4.classicalb.core.parser.node.ACoupleExpression;
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;
import de.be4.classicalb.core.parser.node.ADivExpression;
import de.be4.classicalb.core.parser.node.ADomainExpression;
import de.be4.classicalb.core.parser.node.ADomainRestrictionExpression;
import de.be4.classicalb.core.parser.node.ADomainSubtractionExpression;
import de.be4.classicalb.core.parser.node.AEmptySequenceExpression;
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEquivalencePredicate;
import de.be4.classicalb.core.parser.node.AExistsPredicate;
import de.be4.classicalb.core.parser.node.AFalsityPredicate;
import de.be4.classicalb.core.parser.node.AFirstExpression;
import de.be4.classicalb.core.parser.node.AForallPredicate;
import de.be4.classicalb.core.parser.node.AFunctionExpression;
import de.be4.classicalb.core.parser.node.AGeneralConcatExpression;
import de.be4.classicalb.core.parser.node.AGeneralIntersectionExpression;
import de.be4.classicalb.core.parser.node.AGeneralUnionExpression;
import de.be4.classicalb.core.parser.node.AGreaterEqualPredicate;
import de.be4.classicalb.core.parser.node.AGreaterPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AImplicationPredicate;
import de.be4.classicalb.core.parser.node.AIntSetExpression;
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.AIntersectionExpression;
import de.be4.classicalb.core.parser.node.AIntervalExpression;
import de.be4.classicalb.core.parser.node.AIterationExpression;
import de.be4.classicalb.core.parser.node.ALessEqualPredicate;
import de.be4.classicalb.core.parser.node.ALessPredicate;
import de.be4.classicalb.core.parser.node.AMaxExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.AMinusOrSetSubtractExpression;
import de.be4.classicalb.core.parser.node.AModuloExpression;
import de.be4.classicalb.core.parser.node.AMultiplicationExpression;
import de.be4.classicalb.core.parser.node.ANat1SetExpression;
import de.be4.classicalb.core.parser.node.ANatSetExpression;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.ANotEqualPredicate;
import de.be4.classicalb.core.parser.node.ANotMemberPredicate;
import de.be4.classicalb.core.parser.node.AOverwriteExpression;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.APowerOfExpression;
import de.be4.classicalb.core.parser.node.ARangeExpression;
import de.be4.classicalb.core.parser.node.ARangeRestrictionExpression;
import de.be4.classicalb.core.parser.node.ARangeSubtractionExpression;
import de.be4.classicalb.core.parser.node.ARecEntry;
import de.be4.classicalb.core.parser.node.ARecExpression;
import de.be4.classicalb.core.parser.node.ARecordFieldExpression;
import de.be4.classicalb.core.parser.node.ARelationsExpression;
import de.be4.classicalb.core.parser.node.ARevExpression;
import de.be4.classicalb.core.parser.node.AReverseExpression;
import de.be4.classicalb.core.parser.node.ASeq1Expression;
import de.be4.classicalb.core.parser.node.ASequenceExtensionExpression;
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.ASizeExpression;
import de.be4.classicalb.core.parser.node.AStringExpression;
import de.be4.classicalb.core.parser.node.AStringSetExpression;
import de.be4.classicalb.core.parser.node.AStructExpression;
import de.be4.classicalb.core.parser.node.ASubsetPredicate;
import de.be4.classicalb.core.parser.node.ASubsetStrictPredicate;
import de.be4.classicalb.core.parser.node.ATailExpression;
import de.be4.classicalb.core.parser.node.AUnaryMinusExpression;
import de.be4.classicalb.core.parser.node.AUnionExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PRecEntry;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.TIntegerLiteral;
import de.be4.classicalb.core.parser.node.TStringLiteral;

public class VdmToBConverter extends DepthFirstAnalysisAdaptorAnswer<Node>
{
	public static final String QUOTE_LIT_PREFIX = "QUOTE_LIT_";

	public static final String STATE_ID_PREFIX = "$";

	public static final String OLD_POST_FIX = "~";

	public static final String QUOTES_SET = "QUOTES";

	/**
	 * This adds the state-init expression to the translated constraint
	 */
	public static boolean USE_INITIAL_FIXED_STATE = false;

	/**
	 * future use
	 */
	private final List<PPredicate> constraints = new ArrayList<PPredicate>();

	/**
	 * The console which should be used for printing
	 */
	private SolverConsole console;

	/**
	 * Set used to collect the names of all non supported classes
	 */
	public Set<String> unsupportedConstructs = new HashSet<String>();

	/**
	 * A map that holds substitution rules.
	 * <p>
	 * This may be a mapping from an old state s~ to the old state name with that field e.g. $s's
	 * </p>
	 */
	Map<String, String> nameSubstitution = new HashMap<String, String>();

	/**
	 * The type used for token type defintions. This can be calculated for a complete specification by scanning all
	 * mk_token(a) expressions and combinging the typeof(a) in a union
	 */
	private final PType tokenType;

	public VdmToBConverter()
	{
		console = new SolverConsole();
		tokenType = new ANatNumericBasicType();
	}

	public VdmToBConverter(SolverConsole console, PType tokenType)
	{
		this.console = console;
		this.tokenType = tokenType;
	}

	/**
	 * reserved for future use
	 * 
	 * @param n
	 * @return
	 */
	public Node addCollectedConstraints(PPredicate n)
	{
		PPredicate res = n;
		for (PPredicate c : constraints)
		{
			res = new AConjunctPredicate(res, c);
		}
		return res;
	}

	/**
	 * Utility method to translate to an expression
	 * 
	 * @param n
	 * @return
	 * @throws AnalysisException
	 */
	public PExpression exp(INode n) throws AnalysisException
	{
		Node result = n.apply(this);
		if (result instanceof PPredicate)
		{
			result = new AConvertBoolExpression((PPredicate) result);
		}

		return (PExpression) result;
	}

	/**
	 * Utility method to translate to a predicate
	 * 
	 * @param n
	 * @return
	 * @throws AnalysisException
	 */
	private PPredicate pred(INode n) throws AnalysisException
	{
	    Node result = n.apply(this);
		if (result instanceof PExpression)
		{
			if (result instanceof PExpression)
			{

				if (result instanceof ABooleanTrueExpression)
				{
					// TODO clean this later
					return new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression());// new
																											// ATruthPredicate();
				} else if (result instanceof ABooleanFalseExpression)
				{
					return new AFalsityPredicate();
				}
			}
		}

		return (PPredicate) result;
	}

	@Override
	public Node caseASetEnumSetExp(ASetEnumSetExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression set = new ASetExtensionExpression();
		for (PExp m : node.getMembers())
		{
			set.getExpressions().add(exp(m));
		}

		return set;
	}

	@Override
	public Node caseASetCompSetExp(ASetCompSetExp node)
			throws AnalysisException
	{
		AComprehensionSetExpression scs = new AComprehensionSetExpression();

		LinkedList<PMultipleBind> blist = node.getBindings();
		scs.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		scs.setPredicates(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));

		for (int i = 1; i < blist.size(); i++)
		{
			for (int j = 0; j < blist.get(i).getPlist().size(); j++)
			{
				scs.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
				scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
			}
		}
		List<TIdentifierLiteral> ident = new ArrayList<TIdentifierLiteral>();
		ident.add(new TIdentifierLiteral("_target_"));
		PExpression temp = new AIdentifierExpression(ident);
		scs.getIdentifiers().add(temp);

		scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), pred(node.getPredicate())));
		scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), new AEqualPredicate(temp, exp(node.getFirst()))));
		// { x+y | x in set A, y in set B & P } -> { x, y, _target_ | x : A & y : B & P & _target = x+y }

		return new ARangeExpression(scs);
	}

	@Override
	public Node caseAIfExp(AIfExp node)// TODO: under construction
			throws AnalysisException
	{

		ADisjunctPredicate dp = new ADisjunctPredicate();
		if (node.getElseList().size() == 0)
		{

			dp = new ADisjunctPredicate(new AConjunctPredicate(pred(node.getTest()), pred(node.getThen())), new AConjunctPredicate(new ANegationPredicate(pred(node.getTest())), pred(node.getElse())));
		} else
		{
			LinkedList<AElseIfExp> eilist = node.getElseList();
			eilist.get(eilist.size() - 1).getElseIf();
			eilist.get(eilist.size() - 1).getThen();
			ADisjunctPredicate elsePart = new ADisjunctPredicate();
			elsePart = new ADisjunctPredicate(new AConjunctPredicate(pred(eilist.get(eilist.size() - 1).getElseIf()), pred(eilist.get(eilist.size() - 1).getThen())), new AConjunctPredicate(new ANegationPredicate(pred(eilist.get(eilist.size() - 1).getElseIf())), pred(node.getElse())));
			for (int i = eilist.size() - 2; i >= 0; i--)
			{
				elsePart = new ADisjunctPredicate(new AConjunctPredicate(pred(eilist.get(i).getElseIf()), pred(eilist.get(i).getThen())), new AConjunctPredicate(new ANegationPredicate(pred(eilist.get(i).getElseIf())), elsePart));
			}
			dp = new ADisjunctPredicate(new AConjunctPredicate(pred(node.getTest()), pred(node.getThen())), new AConjunctPredicate(new ANegationPredicate(pred(node.getTest())), elsePart));

		}
		return dp;
	}

	@Override
	public Node caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
			throws AnalysisException
	{

		return new AConjunctPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseAEqualsBinaryExp(AEqualsBinaryExp node)
			throws AnalysisException
	{
		return new AEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASetUnionBinaryExp(ASetUnionBinaryExp node)
			throws AnalysisException
	{
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAInSetBinaryExp(AInSetBinaryExp node)
			throws AnalysisException
	{
		return new AMemberPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASubsetBinaryExp(ASubsetBinaryExp node)
			throws AnalysisException
	{
		return new ASubsetPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseACardinalityUnaryExp(ACardinalityUnaryExp node)
			throws AnalysisException
	{
		return new ACardExpression(exp(node.getExp()));
	}

	@Override
	public Node caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node)
			throws AnalysisException
	{
		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAVariableExp(AVariableExp node) throws AnalysisException
	{

		addTypeConstraint(node.getName(), node.getType());

		return getIdentifier(node.getName());
	}

	/**
	 * Future use: Utility method to add type invariants
	 * 
	 * @param name
	 * @param type
	 */
	private void addTypeConstraint(ILexNameToken name, PType type)
	{
		if (type instanceof ASetType
				&& ((ASetType) type).getSetof() instanceof ANamedInvariantType)
		{
			PType t = ((ASetType) type).getSetof();
			constraints.add(new AMemberPredicate(getIdentifier(name), getBaseType(name, t)));
		}

	}

	private PExpression getBaseType(
			ILexNameToken name/* possibly going to be used to collect inv constraints */,
			PType type)
	{
		if (type instanceof ANamedInvariantType)
		{
			return getBaseType(name, ((ANamedInvariantType) type).getType());
		}
		return null;
	}

	/**
	 * Create an identifier based on a lex name. This method handles old correctly
	 * 
	 * @param name
	 * @return
	 */
	private PExpression getIdentifier(ILexNameToken name)
	{
		String n = name.getName();
		if (name.getOld())
		{
			n += OLD_POST_FIX;
		}
		return getIdentifier(n);
	}

	/**
	 * Construct an identifier from a string using the recorded substitution rules
	 * 
	 * @param n
	 * @return
	 */
	private PExpression getIdentifier(String n)
	{
		while (nameSubstitution.containsKey(n))
		{
			n = nameSubstitution.get(n);
		}

		return createIdentifier(n);
	}

	/**
	 * Creates either an identifier or a field expression if the string contains a quote
	 * 
	 * @param n
	 * @return
	 */
	public static PExpression createIdentifier(String n)
	{
		if (n.contains("\'"))
		{
			String[] elements = n.split("\'");
			return new ARecordFieldExpression(createIdentifier(elements[0]), createIdentifier(elements[1]));
		}

		List<TIdentifierLiteral> ident = new ArrayList<TIdentifierLiteral>();
		ident.add(new TIdentifierLiteral(n));

		return new AIdentifierExpression(ident);
	}

	public static String getQuoteLiteralName(String name)
	{
		return QUOTE_LIT_PREFIX + name;
	}

	/**
	 * Creates state names
	 * 
	 * @param node
	 * @param old
	 * @return
	 */
	public static String getStateId(PDefinition node, boolean old)
	{
		String name = STATE_ID_PREFIX + node.getName().getName().toLowerCase();
		if (old)
		{
			name += OLD_POST_FIX;
		}
		return name;

	}

	public static LexNameToken getStateIdToken(PDefinition node, boolean old)
	{
		String name = STATE_ID_PREFIX + node.getName().getName().toLowerCase();
		if (old)
		{
			name += OLD_POST_FIX;
		}
		return new LexNameToken("", name, node.getLocation());

	}

	@Override
	public Node caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node)
			throws AnalysisException
	{
		return new ADisjunctPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotUnaryExp(ANotUnaryExp node) throws AnalysisException
	{
		return new ANegationPredicate(pred(node.getExp()));
	}

	@Override
	public Node caseABooleanConstExp(ABooleanConstExp node)// FIXME: not yet check
			throws AnalysisException
	{

		if (node.getValue().getValue())
		{
			return new ABooleanTrueExpression();
		} else
		{
			return new ABooleanFalseExpression();
		}
	}

	@Override
	public Node caseAPlusNumericBinaryExp(APlusNumericBinaryExp node)
			throws AnalysisException
	{

		return new AAddExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node)
			throws AnalysisException
	{

		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseATimesNumericBinaryExp(ATimesNumericBinaryExp node)
			throws AnalysisException
	{

		return new AMultiplicationExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivideNumericBinaryExp(ADivideNumericBinaryExp node)
			throws AnalysisException
	{

		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivNumericBinaryExp(ADivNumericBinaryExp node)
			throws AnalysisException
	{
		// x div y = x / y
		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARemNumericBinaryExp(ARemNumericBinaryExp node)
			throws AnalysisException
	{
		// x rem y = x - y * (x/y)
		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), new AMultiplicationExpression(exp(node.getRight()), new ADivExpression(exp(node.getLeft()), exp(node.getRight()))));
	}

	@Override
	public Node caseAModNumericBinaryExp(AModNumericBinaryExp node)
			throws AnalysisException
	{

		return new AModuloExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node)
			throws AnalysisException
	{

		return new AUnaryMinusExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node)
			throws AnalysisException
	{

		PExp num = node.getExp();
		ASetExtensionExpression nums = new ASetExtensionExpression();
		nums.getExpressions().add(exp(num));
		nums.getExpressions().add(new AUnaryMinusExpression(exp(num)));
		return new AMaxExpression(nums);
	}

	@Override
	public Node caseAStarStarBinaryExp(AStarStarBinaryExp node)
			throws AnalysisException
	{
		if (node.getLeft().getType().toString().indexOf("map") == 0)
		{
			// for map
			return new AIterationExpression(exp(node.getLeft()), exp(node.getRight()));
		} else
		{
			// for numeric
			return new APowerOfExpression(exp(node.getLeft()), exp(node.getRight()));
		}
	}

	@Override
	public Node caseALessNumericBinaryExp(ALessNumericBinaryExp node)
			throws AnalysisException
	{
		return new ALessPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node)
			throws AnalysisException
	{
		return new ALessEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node)
			throws AnalysisException
	{
		return new AGreaterPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node) throws AnalysisException
	{
		return new AGreaterEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASetIntersectBinaryExp(ASetIntersectBinaryExp node)
			throws AnalysisException
	{
		return new AIntersectionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADistUnionUnaryExp(ADistUnionUnaryExp node)
			throws AnalysisException
	{
		return new AGeneralUnionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseADistIntersectUnaryExp(ADistIntersectUnaryExp node)
			throws AnalysisException
	{
		return new AGeneralIntersectionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAPowerSetUnaryExp(APowerSetUnaryExp node)
			throws AnalysisException
	{
		return new APowSubsetExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node)
			throws AnalysisException
	{
		return new AImplicationPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotInSetBinaryExp(ANotInSetBinaryExp node)
			throws AnalysisException
	{
		return new ANotMemberPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node)
			throws AnalysisException
	{
		return new ASubsetStrictPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseANotEqualBinaryExp(ANotEqualBinaryExp node)
			throws AnalysisException
	{
		return new ANotEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASeqEnumSeqExp(ASeqEnumSeqExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySequenceExpression();
		}

		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
		for (PExp m : node.getMembers())
		{
			seq.getExpression().add(exp(m));
		}
		return seq;
	}

	@Override
	public Node caseAHeadUnaryExp(AHeadUnaryExp node) throws AnalysisException
	{
		return new AFirstExpression(exp(node.getExp()));
	}

	@Override
	public Node caseATailUnaryExp(ATailUnaryExp node) throws AnalysisException
	{
		return new ATailExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAIndicesUnaryExp(AIndicesUnaryExp node)// FIXME: not yet check
			throws AnalysisException
	{
		LinkedList<PExp> seqmem = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		String size = new String(new Integer(seqmem.size()).toString());
		return new AIntervalExpression(new AIntegerExpression(new TIntegerLiteral("1")), new AIntegerExpression(new TIntegerLiteral(size)));
	}

	@Override
	public Node caseAElementsUnaryExp(AElementsUnaryExp node)
			throws AnalysisException
	{
		if (node.getExp().equals("[]"))
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression aset = new ASetExtensionExpression();
		LinkedList<PExp> seqmem = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		for (PExp e : seqmem)
		{

			aset.getExpressions().add(exp(e));
		}

		return aset;
	}

	@Override
	public Node caseALenUnaryExp(ALenUnaryExp node) throws AnalysisException
	{
		return new ASizeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAReverseUnaryExp(AReverseUnaryExp node) // not yet checked
			throws AnalysisException
	{
		return new ARevExpression(exp(node.getExp()));
	}

	@Override
	public Node caseASeqConcatBinaryExp(ASeqConcatBinaryExp node)
			throws AnalysisException
	{
		return new AConcatExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	// method for map
	@Override
	public Node caseAMapletExp(AMapletExp node) throws AnalysisException
	{
		ACoupleExpression cpl = new ACoupleExpression();
		cpl.getList().add(exp(node.getLeft()));
		cpl.getList().add(exp(node.getRight()));
		return cpl;
	}

	@Override
	public Node caseAMapEnumMapExp(AMapEnumMapExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression map = new ASetExtensionExpression();
		for (AMapletExp m : node.getMembers())
		{
			map.getExpressions().add(exp(m));
		}

		return map;
	}

	@Override
	public Node caseAMapCompMapExp(AMapCompMapExp node)
			throws AnalysisException
	{
	    AComprehensionSetExpression mapcomp = new AComprehensionSetExpression();

	    AMapletExp maplet = node.getFirst();
	    List<PMultipleBind> bindings = node.getBindings();

	    //{a|->a+1 | a in set {1,2,3] & true } --> {from, to | a : {1,2,3} & true & _from = a & _to_ = a+1 }
	    List<TIdentifierLiteral> ident1 = new ArrayList<TIdentifierLiteral>();
	    List<TIdentifierLiteral> ident2 = new ArrayList<TIdentifierLiteral>();
  	    ident1.add(new TIdentifierLiteral("_from_"));
  	    ident2.add(new TIdentifierLiteral("_to_"));
	    PExpression from = new AIdentifierExpression(ident1);
	    PExpression to = new AIdentifierExpression(ident2);
	    mapcomp.getIdentifiers().add(from);
	    mapcomp.getIdentifiers().add(to);

	    mapcomp.getIdentifiers().add(exp(bindings.get(0).getPlist().get(0)));
	    mapcomp.setPredicates(new AMemberPredicate(exp(bindings.get(0).getPlist().get(0)), exp(bindings.get(0))));

	    for (int i = 1; i < bindings.size(); i++)
	    {
		for (int j = 0; j < bindings.get(i).getPlist().size(); j++)
		{
		    mapcomp.getIdentifiers().add(exp(bindings.get(i).getPlist().get(j)));
	            mapcomp.setPredicates(new AConjunctPredicate(
						mapcomp.getPredicates(), 
						new AMemberPredicate(exp(bindings.get(i).getPlist().get(j)), exp(bindings.get(i)))));
		}
	    }

	    mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), pred(node.getPredicate())));
	    mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(from, exp(maplet.getLeft()))));
	    mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(to, exp(maplet.getRight()))));

	    return new ADomainExpression(mapcomp);
	}

	@Override
	public Node caseAMapDomainUnaryExp(AMapDomainUnaryExp node)
			throws AnalysisException
	{
		return new ADomainExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapRangeUnaryExp(AMapRangeUnaryExp node)
			throws AnalysisException
	{
		return new ARangeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapUnionBinaryExp(AMapUnionBinaryExp node)
			throws AnalysisException
	{
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAPlusPlusBinaryExp(APlusPlusBinaryExp node)
			throws AnalysisException
	{
		// seq ++ map
		// map ++ map
		return new AOverwriteExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResToBinaryExp(ADomainResToBinaryExp node)
			throws AnalysisException
	{
		return new ADomainRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResByBinaryExp(ADomainResByBinaryExp node)
			throws AnalysisException
	{
		return new ADomainSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResToBinaryExp(ARangeResToBinaryExp node)
			throws AnalysisException
	{
		return new ARangeRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResByBinaryExp(ARangeResByBinaryExp node)
			throws AnalysisException
	{
		return new ARangeSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAApplyExp(AApplyExp node) throws AnalysisException
	{
		List<PExpression> args = new Vector<PExpression>();

		for (PExp arg : node.getArgs())
		{
		    args.add(exp(arg));
		}

		return new AFunctionExpression(exp(node.getRoot()), args);
	}

	@Override
	public Node caseACompBinaryExp(ACompBinaryExp node)
			throws AnalysisException
	{
		return new ACompositionExpression(exp(node.getRight()), exp(node.getLeft()));
	}

	@Override
	public Node caseAMapInverseUnaryExp(AMapInverseUnaryExp node)
			throws AnalysisException
	{
		return new AReverseExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAForAllExp(AForAllExp node) throws AnalysisException
	{
		AForallPredicate fap = new AForallPredicate();
		LinkedList<PMultipleBind> blist = node.getBindList();

		fap.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		fap.setImplication(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		if (blist.size() > 1)
		{
			for (int i = 1; i < blist.size(); i++)
			{
				for (int j = 0; j < blist.get(i).getPlist().size(); j++)
				{
					fap.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					fap.setImplication(new AConjunctPredicate(fap.getImplication(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		fap.setImplication(new AImplicationPredicate(fap.getImplication(), pred(node.getPredicate())));

		return fap;
	}

	@Override
	public Node caseAExistsExp(AExistsExp node) throws AnalysisException
	{
		AExistsPredicate esp = new AExistsPredicate();
		LinkedList<PMultipleBind> blist = node.getBindList();

		esp.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		esp.setPredicate(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		if (blist.size() > 1)
		{
			for (int i = 1; i < blist.size(); i++)
			{
				for (int j = 0; j < blist.get(i).getPlist().size(); j++)
				{
					esp.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), pred(node.getPredicate())));

		return esp;
	}

	@Override
	public Node caseAExists1Exp(AExists1Exp node) throws AnalysisException
	{
		// exists1 x in set S & pred -> card( { x | x : S & pred } ) = 1

		AIntegerExpression one = new AIntegerExpression(new TIntegerLiteral(new String(new Integer("1").toString())));
		AComprehensionSetExpression cse = new AComprehensionSetExpression();

		cse.setPredicates(new AMemberPredicate(exp(node.getBind().getPattern()), exp(((ASetBind) node.getBind()).getSet())));

		cse.getIdentifiers().add(exp(node.getBind().getPattern()));
		cse.setPredicates(new AConjunctPredicate(cse.getPredicates(), pred(node.getPredicate())));
		AEqualPredicate equal = new AEqualPredicate(new ACardExpression(cse), one);

		return equal;

	}

	@Override
	public Node caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		AIdentifierExpression aie = new AIdentifierExpression();
		aie.getIdentifier().add(new TIdentifierLiteral(node.getName().toString()));
		return aie;
	}

	@Override
	public Node caseASetMultipleBind(ASetMultipleBind node)
			throws AnalysisException
	{

		if (((ASetEnumSetExp) node.getSet()).getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression set = new ASetExtensionExpression();
		for (PExp m : ((ASetEnumSetExp) node.getSet()).getMembers())
		{
			set.getExpressions().add(exp(m));
		}

		return set;
	}

	@Override
	public Node caseAEquivalentBooleanBinaryExp(AEquivalentBooleanBinaryExp node)
			throws AnalysisException
	{
		return new AEquivalencePredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseADistConcatUnaryExp(ADistConcatUnaryExp node)
			throws AnalysisException
	{

		LinkedList<PExp> seqlist = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		if (seqlist.isEmpty())
		{
			return new AEmptySequenceExpression();
		}

		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
		LinkedList<PExp> temp = new LinkedList<PExp>();

		for (PExp m : seqlist)
		{
		    seq.getExpression().add(exp(m));
		}
		return new AGeneralConcatExpression(seq);
	}

	@Override
	public Node caseATupleExp(ATupleExp node) throws AnalysisException
	{
	    // It is necessary that tyeps of all arguments are same.
		LinkedList<PExp> args = node.getArgs();
		//System.err.println("in caseATupleExp: linkedlist: " + args);
		ACoupleExpression cpl = new ACoupleExpression();
		for(PExp elem : args) {
		    cpl.getList().add(exp(elem));
		}
		/*
		cpl.getList().add(exp(args.get(0)));
		cpl.getList().add(exp(args.get(1)));
		*/
		//System.err.println("in caseATupleExp: linkedlist: " + cpl);
		return cpl;

	}

	// StateDefinition
	@Override
	public Node caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		String name = getStateId(node, false);
		String nameOld = getStateId(node, true);

		PPredicate before = new AMemberPredicate(getIdentifier(nameOld), new AStructExpression(getEntities(node.getFields())));
		PPredicate after = new AMemberPredicate(getIdentifier(name), new AStructExpression(getEntities(node.getFields())));
		PPredicate p = new AConjunctPredicate(before, after);

		if (node.getInitExpression() != null && USE_INITIAL_FIXED_STATE)
		{
			PExpression right = (PExpression) ((AEqualsBinaryExp) node.getInitExpression()).getRight().apply(this);
			AEqualPredicate init = new AEqualPredicate(getIdentifier(nameOld), right);
			p = new AConjunctPredicate(p, init);
		}

		for (AFieldField f : node.getFields())
		{
			nameSubstitution.put(f.getTagname().getName() + OLD_POST_FIX, nameOld
					+ "'" + f.getTagname().getName());
			nameSubstitution.put(f.getTagname().getName(), name + "'"
					+ f.getTagname().getName());
		}

		if (node.getInvExpression() != null)
		{
			Map<String, String> nameSubLocal = new HashMap<String, String>();
			Map<String, String> nameSubLocalOld = new HashMap<String, String>();
			for (List<PPattern> plist : node.getInvdef().getParamPatternList())
			{
				for (PPattern pr : plist)
				{
					if (pr instanceof ARecordPattern)
					{
						ARecordPattern record = (ARecordPattern) pr;
						Iterator<PPattern> itrPattern = record.getPlist().iterator();
						Iterator<AFieldField> itrField = node.getFields().iterator();
						while (itrPattern.hasNext())
						{
							String thisP = itrPattern.next().toString();
							String thisF = itrField.next().getTagname().getName();
							nameSubLocal.put(thisP, thisF);
							nameSubLocalOld.put(thisP, thisF + OLD_POST_FIX);
						}
					}
				}
			}

			Node inv = applyWithSubstitution(nameSubLocal, node.getInvExpression());
			Node invOld = applyWithSubstitution(nameSubLocalOld, node.getInvExpression());
			PPredicate invs = new AConjunctPredicate((PPredicate) inv, (PPredicate) invOld);
			p = new AConjunctPredicate(p, invs);
		}

		return p;
	}

	@Override
	public Node caseAMkTypeExp(AMkTypeExp node) throws AnalysisException
	{
	    System.err.println("In caseAMkTypeExp: " + node);
		if (node.getType() instanceof ARecordInvariantType)// properly needs type compare
		{
			List<PRecEntry> entities = new ArrayList<PRecEntry>();

			Iterator<PExp> argItr = node.getArgs().iterator();
			Iterator<AFieldField> fieldItr = node.getRecordType().getFields().iterator();

			while (argItr.hasNext() && fieldItr.hasNext())
			{
				entities.add(new ARecEntry(getIdentifier(fieldItr.next().getTag()), exp(argItr.next())));
			}

			Node r = new ARecExpression(entities);

			return r;
		}
		return super.caseAMkTypeExp(node);
	}

	@Override
	public Node caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
			throws AnalysisException
	{
		String name = getStateId(node, false).replace('$', ' ').trim();
		String nameOld = getStateId(node, true).replace('$', ' ').trim();

		PPredicate before = new AMemberPredicate(getIdentifier(nameOld), exp(node.getType()));
		PPredicate after = new AMemberPredicate(getIdentifier(name), exp(node.getType()));
		PPredicate p = new AConjunctPredicate(before, after);

		return p;
	}

	@Override
	public Node caseAClassInvariantDefinition(AClassInvariantDefinition node)
			throws AnalysisException
	{

		SClassDefinition classDef = node.getAncestor(SClassDefinition.class);

		Map<String, String> newToOldNamesReplace = new HashMap<String, String>();

		for (PDefinition def : classDef.getDefinitions())
		{
			if (def instanceof AInstanceVariableDefinition)
			{
				newToOldNamesReplace.put(getStateId(def, false).replace('$', ' ').trim(), getStateId(def, true).replace('$', ' ').trim());
			}
		}

		nameSubstitution.putAll(newToOldNamesReplace);
		PPredicate old = pred(node.getExpression());

		for (String key : newToOldNamesReplace.keySet())
		{
			nameSubstitution.remove(key);
		}

		return new AConjunctPredicate(old, pred(node.getExpression()));
	}

	@Override
	public Node defaultSOperationDefinition(SOperationDefinition node)
			throws AnalysisException
	{
		PExp post = node.getPostcondition();

		return post.apply(this);
	}

	@Override
	public Node caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node) throws AnalysisException
	{
		PPredicate post = (PPredicate) defaultSOperationDefinition(node);

		Set<ILexNameToken> constants = new HashSet<ILexNameToken>();
		for (AExternalClause frameClause : node.getExternals())
		{
			if (frameClause.getMode().is(VDMToken.READ))
			{
				constants.addAll(frameClause.getIdentifiers());
			}
		}

		// TODO: add the rest of the constants as well. If a state component isn't mentioned in the frame condition it
		// is implicit Read only

		for (ILexNameToken id : constants)
		{
			PPredicate conjoin = new AEqualPredicate(getIdentifier(id), getIdentifier(id.getName()
					+ OLD_POST_FIX));

			post = new AConjunctPredicate(post, conjoin);

		}

		return post;
	}

	private Node applyWithSubstitution(Map<String, String> substitution,
			INode node) throws AnalysisException
	{
		nameSubstitution.putAll(substitution);
		Node inv = node.apply(this);
		for (String n : substitution.keySet())
		{
			nameSubstitution.remove(n);
		}
		return inv;
	}

	private List<PRecEntry> getEntities(LinkedList<AFieldField> fields)
			throws AnalysisException
	{
		List<PRecEntry> entities = new ArrayList<PRecEntry>();
		for (AFieldField f : fields)
		{
			entities.add(new ARecEntry(getIdentifier(f.getTagname()), exp(f.getType())));
		}
		return entities;
	}

	@Override
	public Node caseAIntLiteralExp(AIntLiteralExp node)
			throws AnalysisException
	{
		return new AIntegerExpression(new TIntegerLiteral(""
				+ node.getValue().getValue()));
	}

	@Override
	public Node caseAMkBasicExp(AMkBasicExp node) throws AnalysisException
	{
	    // System.err.println("In caseAMkBasicExp: " + node + " -- " + node.getArg());
		if (node.getType() instanceof ATokenBasicType)
		{
			return node.getArg().apply(this);
		}
		return super.caseAMkBasicExp(node);
	}

	@Override
	public Node caseAQuoteLiteralExp(AQuoteLiteralExp node)
			throws AnalysisException
	{
		return createIdentifier(getQuoteLiteralName(node.getValue().getValue()));
	}

	@Override
	public Node caseAStringLiteralExp(AStringLiteralExp node)
			throws AnalysisException
	{
	    return new AStringExpression(new TStringLiteral(node.getValue().getValue()));
	}

	/* types */

	@Override
	public Node caseABooleanBasicType(ABooleanBasicType node)
			throws AnalysisException
	{
		return new ABoolSetExpression();
	}

	@Override
	public Node caseACharBasicType(ACharBasicType node)
			throws AnalysisException
	{
	    return new AStringExpression();
	}


	@Override
	public Node caseAIntNumericBasicType(AIntNumericBasicType node)
			throws AnalysisException
	{
		return new AIntSetExpression();
	}

	@Override
	public Node caseASetType(ASetType node) throws AnalysisException
	{
		return new APowSubsetExpression(exp(node.getSetof()));
	}

	@Override
	public Node caseASeqSeqType(ASeqSeqType node) throws AnalysisException
	{
		return new ASeq1Expression(exp(node.getSeqof()));
	}

	@Override
	public Node caseANamedInvariantType(ANamedInvariantType node)
			throws AnalysisException
	{
		// TODO: handle inv
		return node.getType().apply(this);

	}

	@Override
	public Node caseANatNumericBasicType(ANatNumericBasicType node)
			throws AnalysisException
	{
		return new ANatSetExpression();
	}

	@Override
	public Node caseANatOneNumericBasicType(ANatOneNumericBasicType node)
			throws AnalysisException
	{
		return new ANat1SetExpression();
	}

	@Override
	public Node caseAMapMapType(AMapMapType node) throws AnalysisException
	{
		return new ARelationsExpression(exp(node.getFrom()), exp(node.getTo()));
	}

	@Override
	public Node caseASeq1SeqType(ASeq1SeqType node) throws AnalysisException
	    {
		if (node.getSeqof() instanceof ACharBasicType)
		{
		    return new AStringSetExpression();
		}

		return new ASeq1Expression(exp(node.getSeqof()));
	}

	@Override
	public Node caseATokenBasicType(ATokenBasicType node)
			throws AnalysisException
	{
		return tokenType.apply(this);
	}

	@Override
	public Node caseAQuoteType(AQuoteType node) throws AnalysisException
	{
		final List<PExpression> exps = Arrays.asList(new PExpression[] { createIdentifier(getQuoteLiteralName(node.getValue().getValue())) });
		return new ASetExtensionExpression(exps);
	}

	/*
	@Override
	public Node caseAProductType(AProductType node) throws AnalysisException
	{
	    return
	}
	*/
	/**
	 * Unknown types may exist in a type check VDM specification as the inner type for e.g. set, seq etc.
	 * <p>
	 * Since a {@code a:POW( ) & z = a union "true"} is valid and makes a partly unbound this can be used for set and
	 * seq
	 * </p>
	 */
	@Override
	public Node caseAUnknownType(AUnknownType node) throws AnalysisException
	{
		if (node.parent() instanceof ASetType)
		{
			return new AEmptySetExpression();
		} else if (node.parent() instanceof SSeqType)
		{
			return new AEmptySequenceExpression();
		}
		return super.caseAUnknownType(node);
	}

	@Override
	public Node createNewReturnValue(INode node) throws AnalysisException
	{

		String name = "_Not-tranclated:" + node.getClass().getSimpleName()
				+ "_";
		console.err.println(name + " " + node);
		unsupportedConstructs.add(node.getClass().getSimpleName());

		List<TIdentifierLiteral> ident = new ArrayList<TIdentifierLiteral>();
		ident.add(new TIdentifierLiteral(name));

		return new AIdentifierExpression(ident);
	}

	@Override
	public Node createNewReturnValue(Object node) throws AnalysisException
	{
		return null;
	}

	@Override
	public Node mergeReturns(Node original, Node new_)
	{
		return null;
	}
}
