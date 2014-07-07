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
import org.overture.ast.definitions.ATypeDefinition;//today
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
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
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
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetRangeSetExp;
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
import org.overture.ast.expressions.PExpBase;
import org.overture.ast.expressions.SSetExp;
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
import org.overture.ast.types.AUnionType;
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
import de.be4.classicalb.core.parser.node.AMultOrCartExpression;
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
//import de.be4.classicalb.core.parser.node.ASuccessorExpression;
//import de.be4.classicalb.core.parser.node.ATruthPredicate;
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

	/** today

	public Set<String> quotes = new HashSet<String>();
	public List<PExpression> quotestype = new Vector<PExpression>();
	 */
	public final List<PExpression> exps = new ArrayList<PExpression>();
	/**
	 * A map that holds substitution rules.
	 * <p>	 * This may be a mapping from an old state s~ to the old state name with that field e.g. $s's
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

		List<PMultipleBind> blist = node.getBindings();

		scs.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		scs.setPredicates(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));

		for (int j = 1; j < blist.get(0).getPlist().size(); j++)
		{
		    scs.getIdentifiers().add(exp(blist.get(0).getPlist().get(j)));
		    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), 
							     new AMemberPredicate(exp(blist.get(0).getPlist().get(j)), exp(blist.get(0)))));
		}

		for (int i = 1; i < blist.size(); i++)
		{
			for (int j = 0; j < blist.get(i).getPlist().size(); j++)
			{
				scs.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
				scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), 
									 new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
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
	public Node caseAIfExp(AIfExp node)// TODO: under construction (1)then/else part returns not bool, (2)else if part
			throws AnalysisException
	{

	    AConjunctPredicate conjp;

		if (node.getElseList().size() == 0)
		{
		    PPredicate ppred;
		    if(node.getElse().toString().equals("ture")) {
			ppred = new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression());
		    } else if(node.getElse().toString().equals("false")) {
			ppred = new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanFalseExpression());
		    } else {
			ppred = pred(node.getElse());
		    }
		    //System.err.println("node.getElse(): " + "["+node.getElse()+"]");
		    //System.err.println("ppred: " + ppred.toString());
		    conjp = new AConjunctPredicate(new ADisjunctPredicate(new ANegationPredicate(pred(node.getTest())), pred(node.getThen())),
						new ADisjunctPredicate(pred(node.getTest()), ppred));
		} else
		    {// elseif part underconstruction
			LinkedList<AElseIfExp> eilist = node.getElseList();
			eilist.get(eilist.size() - 1).getElseIf();
			eilist.get(eilist.size() - 1).getThen();
			ADisjunctPredicate elsePart = new ADisjunctPredicate();
			elsePart = new ADisjunctPredicate(new AConjunctPredicate(pred(eilist.get(eilist.size() - 1).getElseIf()), pred(eilist.get(eilist.size() - 1).getThen())), new AConjunctPredicate(new ANegationPredicate(pred(eilist.get(eilist.size() - 1).getElseIf())), pred(node.getElse())));
			for (int i = eilist.size() - 2; i >= 0; i--)
			{
				elsePart = new ADisjunctPredicate(new AConjunctPredicate(pred(eilist.get(i).getElseIf()), pred(eilist.get(i).getThen())), new AConjunctPredicate(new ANegationPredicate(pred(eilist.get(i).getElseIf())), elsePart));
			}
			conjp = new AConjunctPredicate(new ADisjunctPredicate(new ANegationPredicate(pred(node.getTest())), pred(node.getThen())), 
						       new ADisjunctPredicate(pred(node.getTest()), elsePart));

		}
		return conjp;
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
	public Node caseASetRangeSetExp(ASetRangeSetExp node)
			throws AnalysisException
	{
	    //fst=node.getFirst();
	    //lst=node.getLast();
	    //{fst,...,lst} -> {x | x:S & fst<=lst & fst<=x & x<=lst }
	    
	    AComprehensionSetExpression scs = new AComprehensionSetExpression();
	    List<TIdentifierLiteral> id1 = new ArrayList<TIdentifierLiteral>();
	    id1.add(new TIdentifierLiteral("_tgt1_"));
	    PExpression temp = new AIdentifierExpression(id1);
	    scs.getIdentifiers().add(temp);
	    scs.setPredicates(new AMemberPredicate(temp, new AIntervalExpression(exp(node.getFirst()), exp(node.getLast()))));
	    return scs;

	    /*
	    AComprehensionSetExpression scs = new AComprehensionSetExpression();
	    List<TIdentifierLiteral> id1 = new ArrayList<TIdentifierLiteral>();
	    id1.add(new TIdentifierLiteral("_tgt1_"));
	    PExpression temp = new AIdentifierExpression(id1);
	    scs.getIdentifiers().add(temp);

	    List<TIdentifierLiteral> id2 = new ArrayList<TIdentifierLiteral>();
	    id2.add(new TIdentifierLiteral("_tgt2_"));
	    PExpression temp2 = new AIdentifierExpression(id2);

	    // scs.setPredicates(new AMemberPredicate(temp, exp(node.getFirst().getFtype())));

	    //scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), new AGreaterEqualPredicate(exp(node.getFirst()), exp(node.getLast()))));
	    scs.setPredicates(new ALessEqualPredicate(exp(node.getFirst()), exp(node.getLast())));
	    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(),
						     new ALessEqualPredicate(exp(node.getFirst()), temp2)));
	    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(),
						     new ALessEqualPredicate(temp2, exp(node.getLast()))));
	    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), new AEqualPredicate(temp, temp2)));

	    return scs;
	    */
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

	    AComprehensionSetExpression scs = new AComprehensionSetExpression();
	    List<TIdentifierLiteral> id1 = new ArrayList<TIdentifierLiteral>();
	    id1.add(new TIdentifierLiteral("_tgt1_"));
	    PExpression temp = new AIdentifierExpression(id1);
	    scs.getIdentifiers().add(temp);
	    scs.setPredicates(new AMemberPredicate(temp, 
						   new AIntervalExpression(new AIntegerExpression(new TIntegerLiteral("1")), 
									   new AIntegerExpression(new TIntegerLiteral(size)))));
	    return scs;

	}

	@Override
	public Node caseAElementsUnaryExp(AElementsUnaryExp node)
			throws AnalysisException
	{
	    //System.err.println("in caseAElementsUnaryExp node: " + node);
	    //System.err.println("in caseAElementsUnaryExp node.getExp(): " + node.getExp());
	    //System.err.println("in caseAElementsUnaryExp node.getExp().apply(this): " + node.getExp().apply(this));

		if (node.getExp().equals("[]"))
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression aset = new ASetExtensionExpression();
		LinkedList<PExp> seqmem = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		// does not work on the case elems [[1,2], [3,4], [5,6]](2)
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
	public Node caseAFieldExp(AFieldExp node)
			throws AnalysisException
	    {//Exp,ILexNameToken
		System.err.println("in caseAFieldExp: " + node + ", object: " + node.getObject() + ", fname: " + node.getField() );
		return new ARecordFieldExpression(exp(node.getObject()), getIdentifier(node.getField().getName()));
		/*
		if(!(node.getField() instanceof AFieldExp)) {
		    return new ARecordFieldExpression(exp(node.getObject()), getIdentifier(node.getField().getName()));
		} else {
		    return ((AFieldExp)(node.getField())).apply(this);
		}
		*/

	}

	@Override
	public Node caseAFieldNumberExp(AFieldNumberExp node)
			throws AnalysisException
	{
	    // mk_(1,2,3).#2 -> [1,2,3](2)
	    ATupleExp tuple = (ATupleExp)node.getTuple();
	    AIntegerExpression arg = new AIntegerExpression(new TIntegerLiteral(node.getField().toString()));
	    List<PExpression> args = new Vector<PExpression>();
	    args.add(arg);

	    ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
	    List<PExpression> list = new Vector<PExpression>();
	    for(PExp elem : tuple.getArgs()) {
		list.add(exp(elem));
	    }

	    seq.setExpression(list);
	    AFunctionExpression fun = new AFunctionExpression();
	    fun.setIdentifier(seq);
	    fun.setParameters(args);
	    return fun;
	}


	/**********
	@Override
	public Node caseASeqCompSeqExp(ASeqCompSeqExp node)
			throws AnalysisException
	{

	    //[e | id in set S & P]
	    // create { id, _target_ | id:S & P & i_target_=e }

	    System.err.println("first: " + node.getFirst()); //e
	    System.err.println("pattern: " + (node.getSetBind()).getPattern()); //id
	    System.err.println("set: " + (node.getSetBind()).getSet()); //S
	    System.err.println("pred: " + node.getPredicate()); //P

	    AComprehensionSetExpression scs = new AComprehensionSetExpression();
            // create a set
	    scs.getIdentifiers().add(exp(node.getSetBind().getPattern()));
	    scs.setPredicates(new AMemberPredicate(exp(node.getSetBind().getPattern()),
                                                   exp(node.getSetBind().getSet())));

	    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), 
                                                     pred(node.getPredicate())));

	    List<TIdentifierLiteral> ident = new ArrayList<TIdentifierLiteral>();
	    ident.add(new TIdentifierLiteral("_target_"));
	    PExpression temp = new AIdentifierExpression(ident);
	    scs.getIdentifiers().add(temp);

	    scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), 
                                              new AEqualPredicate(temp, exp(node.getFirst()))));
	    if(scs.getMembers().isEmpty()) {
		AEmptySequeneExpression seq = new AEmptySequeneExpression();
	    } else {
		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
		for(PExpression elem : scs.getMembers()) {
		    seq.add(elem.getList().getLast());
		}
	    }

	    return seq;
	}
	**********/

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
	    List<PMultipleBind> blist = node.getBindings();
	    // create temporal free variables
	    List<TIdentifierLiteral> ident1 = new ArrayList<TIdentifierLiteral>();
	    List<TIdentifierLiteral> ident2 = new ArrayList<TIdentifierLiteral>();
  	    ident1.add(new TIdentifierLiteral("_from_"));
  	    ident2.add(new TIdentifierLiteral("_to_"));
	    PExpression from = new AIdentifierExpression(ident1);
	    PExpression to = new AIdentifierExpression(ident2);

	    mapcomp.getIdentifiers().add(from);
	    mapcomp.getIdentifiers().add(to);

	    AExistsPredicate esp = new AExistsPredicate();
		esp.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		esp.setPredicate(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		if (blist.size() > 1)
		{
			for (int i = 1; i < blist.size(); i++)
			{
				for (int j = 0; j < blist.get(i).getPlist().size(); j++)
				{
					esp.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					esp.setPredicate(new AConjunctPredicate(esp.getPredicate(),
										new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

	    if(node.getPredicate()==null) {
		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression())));
	    } else {
		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), pred(node.getPredicate())));
	    }
		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AEqualPredicate(from, exp(maplet.getLeft()))));
		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AEqualPredicate(to, exp(maplet.getRight()))));

		mapcomp.setPredicates(esp);

	    AComprehensionSetExpression mapcomp1 = new AComprehensionSetExpression();
	    AMapletExp maplet1 = node.getFirst();
	    List<PMultipleBind> blist1 = node.getBindings();
	    // create temporal free variables
	    List<TIdentifierLiteral> ident11 = new ArrayList<TIdentifierLiteral>();
	    List<TIdentifierLiteral> ident21 = new ArrayList<TIdentifierLiteral>();
  	    ident11.add(new TIdentifierLiteral("_from1_"));
  	    ident21.add(new TIdentifierLiteral("_to1_"));
	    PExpression from1 = new AIdentifierExpression(ident11);
	    PExpression to1 = new AIdentifierExpression(ident21);

	    mapcomp1.getIdentifiers().add(from1);
	    mapcomp1.getIdentifiers().add(to1);

	    AExistsPredicate esp1 = new AExistsPredicate();
		esp1.getIdentifiers().add(exp(blist1.get(0).getPlist().get(0)));
		esp1.setPredicate(new AMemberPredicate(exp(blist1.get(0).getPlist().get(0)), exp(blist1.get(0))));
		if (blist1.size() > 1)
		{
			for (int i = 1; i < blist1.size(); i++)
			{
				for (int j = 0; j < blist1.get(i).getPlist().size(); j++)
				{
					esp1.getIdentifiers().add(exp(blist1.get(i).getPlist().get(j)));
					esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(),
										new AMemberPredicate(exp(blist1.get(i).getPlist().get(j)), exp(blist1.get(i)))));
				}
			}
		}

	    if(node.getPredicate()==null) {
		esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(), new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression())));
	    } else {
		esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(), pred(node.getPredicate())));
	    }
		//esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(), pred(node.getPredicate())));
		esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(), new AEqualPredicate(from1, exp(maplet1.getLeft()))));
		esp1.setPredicate(new AConjunctPredicate(esp1.getPredicate(), new AEqualPredicate(to1, exp(maplet1.getRight()))));

		mapcomp1.setPredicates(esp1);

	    AComprehensionSetExpression mapcomp2 = new AComprehensionSetExpression();
	    AMapletExp maplet2 = node.getFirst();
	    List<PMultipleBind> blist2 = node.getBindings();
	    // create temporal free variables
	    List<TIdentifierLiteral> ident12 = new ArrayList<TIdentifierLiteral>();
	    List<TIdentifierLiteral> ident22 = new ArrayList<TIdentifierLiteral>();
  	    ident12.add(new TIdentifierLiteral("_from2_"));
  	    ident22.add(new TIdentifierLiteral("_to2_"));
	    PExpression from2 = new AIdentifierExpression(ident12);
	    PExpression to2 = new AIdentifierExpression(ident22);

	    mapcomp2.getIdentifiers().add(from2);
	    mapcomp2.getIdentifiers().add(to2);

	    AExistsPredicate esp2 = new AExistsPredicate();
		esp2.getIdentifiers().add(exp(blist2.get(0).getPlist().get(0)));
		esp2.setPredicate(new AMemberPredicate(exp(blist2.get(0).getPlist().get(0)), exp(blist2.get(0))));
		if (blist2.size() > 1)
		{
			for (int i = 1; i < blist2.size(); i++)
			{
				for (int j = 0; j < blist2.get(i).getPlist().size(); j++)
				{
					esp2.getIdentifiers().add(exp(blist2.get(i).getPlist().get(j)));
					esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(),
										new AMemberPredicate(exp(blist2.get(i).getPlist().get(j)), exp(blist2.get(i)))));
				}
			}
		}

	    if(node.getPredicate()==null) {
		esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(), new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression())));
	    } else {
		esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(), pred(node.getPredicate())));
	    }
	    //esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(), pred(node.getPredicate())));
		esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(), new AEqualPredicate(from2, exp(maplet2.getLeft()))));
		esp2.setPredicate(new AConjunctPredicate(esp2.getPredicate(), new AEqualPredicate(to2, exp(maplet2.getRight()))));

		mapcomp2.setPredicates(esp2);


		//return mapcomp; // not a map but a relation

		AForallPredicate forallp = new AForallPredicate();
		List<TIdentifierLiteral> id1 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id2 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id3 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id4 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id5 = new ArrayList<TIdentifierLiteral>();
		id1.add(new TIdentifierLiteral("_pd1_"));
		id2.add(new TIdentifierLiteral("_pr1_"));
		id3.add(new TIdentifierLiteral("_pd2_"));
		id4.add(new TIdentifierLiteral("_pr2_"));
		id5.add(new TIdentifierLiteral("_pair_"));

		PExpression pd1 = new AIdentifierExpression(id1);
		PExpression pr1 = new AIdentifierExpression(id2);
		PExpression pd2 = new AIdentifierExpression(id3);
		PExpression pr2 = new AIdentifierExpression(id4);
		PExpression ppair = new AIdentifierExpression(id5);
		forallp.getIdentifiers().add(pd1);
		forallp.getIdentifiers().add(pr1);
		forallp.getIdentifiers().add(pd2);
		forallp.getIdentifiers().add(pr2);

		AComprehensionSetExpression isdup = new AComprehensionSetExpression();
		ACoupleExpression pair1 = new ACoupleExpression();
		pair1.getList().add(pd1);		pair1.getList().add(pr1);
		ACoupleExpression pair2 = new ACoupleExpression();
		pair2.getList().add(pd2);		pair2.getList().add(pr2);

		isdup.setPredicates(new AMemberPredicate(pair1, mapcomp));
		isdup.setPredicates(new AConjunctPredicate(isdup.getPredicates(),new AMemberPredicate(pair2, mapcomp1)));
		isdup.setPredicates(new AConjunctPredicate(isdup.getPredicates(),new AEqualPredicate(pd1, pd2)));

		AEqualPredicate equal = new AEqualPredicate(pr1, pr2);

		forallp.setImplication(new AImplicationPredicate(isdup.getPredicates(), equal));

		AComprehensionSetExpression realmap = new AComprehensionSetExpression();
		realmap.getIdentifiers().add(ppair);
		realmap.setPredicates(new AConjunctPredicate(forallp, new AMemberPredicate(ppair, mapcomp2)));

		/**********************************************************************************
		AForallPredicate forallp = new AForallPredicate();
		List<TIdentifierLiteral> id1 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id2 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> id3 = new ArrayList<TIdentifierLiteral>();
		id1.add(new TIdentifierLiteral("_pdom_"));
		id2.add(new TIdentifierLiteral("_pran_"));
		id3.add(new TIdentifierLiteral("_map_"));
		PExpression pdom = new AIdentifierExpression(id1);
		PExpression pran = new AIdentifierExpression(id2);
		PExpression singlemaplet = new AIdentifierExpression(id3);
		forallp.getIdentifiers().add(pdom);

		AComprehensionSetExpression isdup = new AComprehensionSetExpression();
		isdup.getIdentifiers().add(pran);
		ACoupleExpression onepair = new ACoupleExpression();
		onepair.getList().add(pdom);		onepair.getList().add(pran);
		isdup.setPredicates(new AMemberPredicate(onepair, mapcomp));

		AIntegerExpression one = new AIntegerExpression(new TIntegerLiteral(new String(new Integer("1").toString())));

		AEqualPredicate equal = new AEqualPredicate(new ACardExpression(isdup), one);

		forallp.setImplication(new AImplicationPredicate(new AMemberPredicate(pdom, new ADomainExpression(mapcomp1)),equal));

		AComprehensionSetExpression realmap = new AComprehensionSetExpression();
		realmap.getIdentifiers().add(singlemaplet);
		realmap.setPredicates(new AConjunctPredicate(forallp, new AMemberPredicate(singlemaplet, mapcomp2)));
		**********************************************************************************/

		return realmap;

		/*************************************************************************************
		List<TIdentifierLiteral> mapid = new ArrayList<TIdentifierLiteral>();
		mapid.add(new TIdentifierLiteral("_mcomp_"));
		PExpression mcomp = new AIdentifierExpression(mapid);
		AEqualPredicate tempmcomp = new AEqualPredicate(mcomp, mapcomp);

		AForallPredicate maprestriction = new AForallPredicate();
		AExistsPredicate maprestpred = new AExistsPredicate();

		List<TIdentifierLiteral> identfa1 = new ArrayList<TIdentifierLiteral>();
		identfa1.add(new TIdentifierLiteral("_map_dom_"));
		PExpression map_dom = new AIdentifierExpression(identfa1);

		maprestriction.getIdentifiers().add(map_dom);
		ADomainExpression dom = new ADomainExpression(mapcomp);

		List<TIdentifierLiteral> identfa2 = new ArrayList<TIdentifierLiteral>();
		List<TIdentifierLiteral> identfa3 = new ArrayList<TIdentifierLiteral>();
		identfa2.add(new TIdentifierLiteral("_map_ran1_"));
		identfa3.add(new TIdentifierLiteral("_map_ran2_"));
		PExpression map_rn1 = new AIdentifierExpression(identfa2);
		PExpression map_rn2 = new AIdentifierExpression(identfa3);

		maprestpred.getIdentifiers().add(map_rn1);
		maprestpred.getIdentifiers().add(map_rn2);
		ACoupleExpression cpl1 = new ACoupleExpression();
		ACoupleExpression cpl2 = new ACoupleExpression();
		cpl1.getList().add(map_dom);cpl1.getList().add(map_rn1);
		cpl2.getList().add(map_dom);cpl2.getList().add(map_rn2);
		maprestpred.setPredicate(
					 new AConjunctPredicate(new AConjunctPredicate(new AMemberPredicate(cpl1, mapcomp), new AMemberPredicate(cpl2, mapcomp)),
								new ANotEqualPredicate(map_rn1, map_rn2)));

		maprestriction.setImplication(new AImplicationPredicate(new AMemberPredicate(map_dom, dom), new ANegationPredicate(maprestpred.getPredicate())));

		return new AConjunctPredicate(tempmcomp, maprestriction);
		*************************************************************************************/

	    /* old type
	    int domtimes=0;
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

	    System.err.println("length of bindings: " + bindings.get(0).getPlist().size());

	    for (int j = 1; j < bindings.get(0).getPlist().size(); j++)
	    {
		domtimes++;
		mapcomp.getIdentifiers().add(exp(bindings.get(0).getPlist().get(j)));
		mapcomp.setPredicates(new AConjunctPredicate(
							     mapcomp.getPredicates(), 
						new AMemberPredicate(exp(bindings.get(0).getPlist().get(j)), exp(bindings.get(0)))));
	    }

	    for (int i = 1; i < bindings.size(); i++)
	    {
		for (int j = 0; j < bindings.get(i).getPlist().size(); j++)
		{
		    domtimes++;
		    mapcomp.getIdentifiers().add(exp(bindings.get(i).getPlist().get(j)));
	            mapcomp.setPredicates(new AConjunctPredicate(
						mapcomp.getPredicates(), 
						new AMemberPredicate(exp(bindings.get(i).getPlist().get(j)), exp(bindings.get(i)))));
		}
	    }

	    if(node.getPredicate()!=null)
		mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), pred(node.getPredicate())));

	    mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(from, exp(maplet.getLeft()))));
	    mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(to, exp(maplet.getRight()))));

	    ADomainExpression domexp = new ADomainExpression(mapcomp);

	    for (int i = 0; i < domtimes; i++)
	    {
		domexp = new ADomainExpression(domexp);
	    }
	    return domexp;
	    */
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
	    //Both two maps assigned as arguments of mnuion are instances of ASetExtensionExpression, we can obtain map as a result
	    if(exp(node.getLeft()) instanceof ASetExtensionExpression && exp(node.getRight()) instanceof ASetExtensionExpression) {
		ASetExtensionExpression s1 = (ASetExtensionExpression) exp(node.getLeft());
		ASetExtensionExpression s2 = (ASetExtensionExpression) exp(node.getRight());
		ASetExtensionExpression s3 = new ASetExtensionExpression();

		for(int d2=0;d2<s2.getExpressions().size();d2++) {
		    boolean uniqueDom=true;
		    //System.err.println("s2: " + ((ACoupleExpression)(s2.getExpressions().get(d2))).getList().get(0));
		    for(int d1=0;d1<s1.getExpressions().size();d1++) {
			//System.err.println("s2: " + s2.getExpressions().get(d2) + " s1: " + s1.getExpressions().get(d1));
			//System.err.println("s1: " + ((ACoupleExpression)(s1.getExpressions().get(d1))).getList().get(0).toString());
			if(((ACoupleExpression)(s2.getExpressions().get(d2))).getList().get(0).toString().equals(
                           ((ACoupleExpression)(s1.getExpressions().get(d1))).getList().get(0).toString())) {
			    uniqueDom = false;
			    System.err.println(uniqueDom);
			    if(s2.getExpressions().get(d2).toString().equals(s1.getExpressions().get(d1).toString())) {
				s3.getExpressions().add(s2.getExpressions().get(d2));
			    }
			}
		    }
		    System.err.println(uniqueDom);
		    if(uniqueDom) {
			s3.getExpressions().add(s2.getExpressions().get(d2));
		    }
		}
		if(s3.getExpressions().size()==0) {
		    s1 = new ASetExtensionExpression();
		}
		return new AUnionExpression(s1, s3);
	    } else {
		//Otherwise
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	    }
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
	    //System.err.println("in caseASetMB: " + node + "  " + node.getSet().getClass());
	    if(node.getSet() instanceof ASetCompSetExp) {
		//return caseASetCompSetExp((ASetCompSetExp) node.getSet());
		return node.getSet().apply(this);
	    }
	    if(node.getSet() instanceof AVariableExp) {
		//return caseAVariableExp((AVariableExp) node.getSet());
		return node.getSet().apply(this);
	    }
	    if(node.getSet() instanceof AMapDomainUnaryExp) {
		//return caseAMapDomainUnaryExp((AMapDomainUnaryExp) node.getSet());
		return node.getSet().apply(this);
	    }
	    if(node.getSet() instanceof AApplyExp) {
		//return caseAApplyExp((AApplyExp) node.getSet());
		return node.getSet().apply(this);
	    }

	    //if (((ASetEnumSetExp) node.getSet()).getMembers().isEmpty())

		if(node.getSet() instanceof ASetEnumSetExp)
		{
			ASetEnumSetExp	setEnum = (ASetEnumSetExp) node.getSet();
			
			if (setEnum.getMembers().isEmpty())
			{
				return new AEmptySetExpression();
			}

			ASetExtensionExpression set = new ASetExtensionExpression();
			for (PExp m : setEnum.getMembers())
			{
				set.getExpressions().add(exp(m));
			}

			return set;
		}else if(node.getSet() instanceof ASetCompSetExp)
		{
			
		}else if(node.getSet() instanceof ASetRangeSetExp)
		{
			
		}

		//error case
		return super.caseASetMultipleBind(node);
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
	    /*
		LinkedList<PExp> args = node.getArgs();
		System.err.println("in caseATupleExp: linkedlist: " + args);//comment out May26
		System.err.println("in caseATupleExp: linkedlist: " + node);//comment out May26
		System.err.println("in caseATupleExp: linkedlist: " + node.getTypes());//comment out May26
	    */
	    /*
		ACoupleExpression cpl = new ACoupleExpression();
		for(PExp elem : node.getArgs()) {
		    cpl.getList().add(exp(elem));
		}
	    */

		ACoupleExpression cpl = new ACoupleExpression();
		ACoupleExpression[] temp = new ACoupleExpression[10];
		temp[0] = new ACoupleExpression();
		temp[0].getList().add(exp(node.getArgs().get(0)));
		System.err.println("type of seq of char: " + node.getArgs().get(0).getType());
		temp[0].getList().add(exp(node.getArgs().get(1)));
		for(int i=2;i<node.getArgs().size();i++) {
		    temp[i-1] = new ACoupleExpression();
		    temp[i-1].getList().add(temp[i-2]);
		    temp[i-1].getList().add(exp(node.getArgs().get(i)));
		}
		cpl=temp[node.getArgs().size()-2];
		System.err.println("in caseATupleExp: linkedlist: " + cpl);

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
			if(node.getInitExpression() instanceof AEqualsBinaryExp)
			{
			PExpression right = (PExpression) ((AEqualsBinaryExp) node.getInitExpression()).getRight().apply(this);
			AEqualPredicate init = new AEqualPredicate(getIdentifier(nameOld), right);
			p = new AConjunctPredicate(p, init);
			}else
			{
				//FIXME: unsupported expression
				
			}
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

	@Override //today
	public Node caseAUnionType(AUnionType node)
			throws AnalysisException
	{
	    System.err.println("in caseAUnionType " + node);

	    return  new ASetExtensionExpression(); //dummy
	}

	@Override //today
	public Node caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
	    System.err.println("in caseATypeDefinition: " + node);
	    System.err.println("in caseATypeDefinition(getInvType: " + node.getInvType());
	    System.err.println("in caseATypeDefinition(getInvPattern: " + node.getInvPattern());
	    System.err.println("in caseATypeDefinition(getInvExpression: " + node.getInvExpression());
	    System.err.println("in caseATypeDefinition(getInvdef: " + node.getInvdef());
	    System.err.println("in caseATypeDefinition(getName: " + node.getName());
	    System.err.println("in caseATypeDefinition(getCompose: " + node.getComposeDefinitions());
	    return new ASetExtensionExpression();//dummy
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
	    //System.err.println("In caseAMkBasicExp: " + node + " -- " + node.getArg());
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
		System.err.println("in caseAQuoteLiteralExp: " + node);
		System.err.println("in caseAQuoteLiteralExp type: " + ((PExpBase)node).getType());
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
	    {System.err.println("in caseASeqSeq: " + node.getSeqof());

		List<TIdentifierLiteral> ident = new ArrayList<TIdentifierLiteral>();
		ident.add(new TIdentifierLiteral("STRING"));
		PExpression temp = new AIdentifierExpression(ident);
		return temp;
	    //return new ASeq1Expression(exp(node.getSeqof()));
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

		System.err.println("in caseAQuoteType: " + node);
		return new ASetExtensionExpression(exps);
	}


	@Override
	public Node caseAProductType(AProductType node) throws AnalysisException
	{
	    //System.err.println("in caseProductType**: " + node);
	    //System.err.println("in caseProductType**: " + node.getTypes());
	    /*
	    ACoupleExpression cpl = new ACoupleExpression();
	    for(PType elem : node.getTypes()) {
		cpl.getList().add(exp(elem));
	    }
	    */
	    
	    if(node.getTypes().get(0).toString().equals("char")) System.err.println("in caseAProductTypes: " + "["+node.getTypes().get(0)+"]");
	    AMultOrCartExpression prod = new AMultOrCartExpression();// create NAT1*NAT1 for example
	    AMultOrCartExpression[] temp = new AMultOrCartExpression[10];//dummpy
	    temp[0] = new AMultOrCartExpression();
	    temp[0].setLeft(exp(node.getTypes().get(0)));
	    temp[0].setRight(exp(node.getTypes().get(1)));
	    for(int i=2;i<node.getTypes().size();i++) {
		temp[i-1] = new AMultOrCartExpression();
		temp[i-1].setLeft(temp[i-2]);
		temp[i-1].setRight(exp(node.getTypes().get(i)));
	    }
	    prod=temp[node.getTypes().size()-2];
	    System.err.println("prod: " + prod);

	    return prod;
	}

	@Override //today
	public Node caseARecordInvariantType(ARecordInvariantType node) throws AnalysisException
	{
	    System.err.println("in caseARecordInvariantType**: " + node);
	    System.err.println("in caseARecordInvariantType**: " + node.getName());
	    Node r = new ARecExpression();
	    return r;
	}
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
