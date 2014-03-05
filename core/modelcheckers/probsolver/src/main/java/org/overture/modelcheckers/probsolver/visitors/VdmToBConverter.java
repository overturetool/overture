package org.overture.modelcheckers.probsolver.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AAbsoluteUnaryExp; //added -> AMaxExpression
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp; //added -> AFunctionExpression(for seq(nat)), AImageExpression(for map(nat)), 
//import org.overture.ast.expressions.ABooleanConstExp;              //added -> ATruethPredicate, AFalsityPredicate
import org.overture.ast.expressions.ABooleanConstExp; //added -> ABooleanTrueExpression, ABooleanFalseExpression
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACompBinaryExp; //added -> ACompositionExpression
import org.overture.ast.expressions.ADistConcatUnaryExp; //added -> AGeneralConcatExpression
import org.overture.ast.expressions.ADistIntersectUnaryExp; //added -> AGeneralIntersectionExpression
import org.overture.ast.expressions.ADistUnionUnaryExp; //added -> AGeneralUnionExpression
import org.overture.ast.expressions.ADivNumericBinaryExp; //added -> ADivExpression
import org.overture.ast.expressions.ADivideNumericBinaryExp; //added -> ADivExpression
import org.overture.ast.expressions.ADomainResByBinaryExp; //added -> ADomainSubtractionExpression
import org.overture.ast.expressions.ADomainResToBinaryExp; //added -> ADomainRestrictionExpression
import org.overture.ast.expressions.AElementsUnaryExp; //used  -> ASetExtensionExpression;
import org.overture.ast.expressions.AElseIfExp; //added
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp; //added -> AEquivalencePeredicate
import org.overture.ast.expressions.AExists1Exp; //added
import org.overture.ast.expressions.AExistsExp; //added -> AExistsPredicate
import org.overture.ast.expressions.AForAllExp; //added -> AForallPredicate
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp; //added -> AGreaterEqualPredicate
import org.overture.ast.expressions.AGreaterNumericBinaryExp; //added -> AGreaterPredicate
//         AEmptySequenceExpression,ASequenceExtensionExpression
import org.overture.ast.expressions.AHeadUnaryExp; //added -> AFirstExpression
import org.overture.ast.expressions.AIfExp; //added
import org.overture.ast.expressions.AImpliesBooleanBinaryExp; //added -> AImplicationPredicate
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp; //added -> AIntervalExpression
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp; //added -> ASizeExpression
import org.overture.ast.expressions.ALessEqualNumericBinaryExp; //added -> ALessEqualPredicate
import org.overture.ast.expressions.ALessNumericBinaryExp; //added -> ALessPredicate
import org.overture.ast.expressions.AMapDomainUnaryExp; //added -> ADomainExpression
import org.overture.ast.expressions.AMapEnumMapExp; //added
import org.overture.ast.expressions.AMapInverseUnaryExp; //added -> AReverseExpression
import org.overture.ast.expressions.AMapRangeUnaryExp; //added -> ARrangeExpression
import org.overture.ast.expressions.AMapUnionBinaryExp; //used  -> AUnionExpression
import org.overture.ast.expressions.AMapletExp; //added -> ACoupleExpression
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp; //added -> AModuleExpression
import org.overture.ast.expressions.ANotEqualBinaryExp; //added -> ANotEqualPredicate
import org.overture.ast.expressions.ANotInSetBinaryExp; //added -> ANotMemberPredicate
import org.overture.ast.expressions.ANotUnaryExp; //added -> ANegationPredicate
import org.overture.ast.expressions.AOrBooleanBinaryExp; //added -> ADisjunctPredicate
import org.overture.ast.expressions.APlusNumericBinaryExp; //added -> AAddExpression
import org.overture.ast.expressions.APlusPlusBinaryExp; //added -> AOverwriteExpression(for map ++ map), (for seq ++ map)
import org.overture.ast.expressions.APowerSetUnaryExp; //added -> APowSubsetExpression
import org.overture.ast.expressions.AProperSubsetBinaryExp; //added -> ASubsetStrictPredicate
import org.overture.ast.expressions.ARangeResByBinaryExp; //added -> ARangeSubtractionExpression
import org.overture.ast.expressions.ARangeResToBinaryExp; //added -> ARangeRestrictionExpression
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp; //added -> AComprehensionSetExpression
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;//added -> AIdentifireExpression
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;//added
import org.overture.ast.patterns.PMultipleBind;//added
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType; //added -> ANat1SetExpression
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.modelcheckers.probsolver.SolverConsole;

import de.be4.classicalb.core.parser.node.AAddExpression;//added
import de.be4.classicalb.core.parser.node.ABoolSetExpression;
import de.be4.classicalb.core.parser.node.ABooleanFalseExpression;//added
import de.be4.classicalb.core.parser.node.ABooleanTrueExpression;//added
import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.ACompositionExpression; //added
import de.be4.classicalb.core.parser.node.AComprehensionSetExpression;//added
import de.be4.classicalb.core.parser.node.AConcatExpression; //added
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AConvertBoolExpression;
import de.be4.classicalb.core.parser.node.ACoupleExpression; //added
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;//added
import de.be4.classicalb.core.parser.node.ADivExpression;//added
import de.be4.classicalb.core.parser.node.ADomainExpression; //added
import de.be4.classicalb.core.parser.node.ADomainRestrictionExpression; //added
import de.be4.classicalb.core.parser.node.ADomainSubtractionExpression; //added
import de.be4.classicalb.core.parser.node.AEmptySequenceExpression; //added
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEquivalencePredicate;//added
import de.be4.classicalb.core.parser.node.AExistsPredicate;//added
import de.be4.classicalb.core.parser.node.AFalsityPredicate;//added
import de.be4.classicalb.core.parser.node.AFirstExpression; //added
import de.be4.classicalb.core.parser.node.AForallPredicate;//added
import de.be4.classicalb.core.parser.node.AGeneralConcatExpression;//added
import de.be4.classicalb.core.parser.node.AGeneralIntersectionExpression; //added
import de.be4.classicalb.core.parser.node.AGeneralUnionExpression; //added
import de.be4.classicalb.core.parser.node.AGreaterEqualPredicate;//added
import de.be4.classicalb.core.parser.node.AGreaterPredicate;//added
import de.be4.classicalb.core.parser.node.AIdentifierExpression;//added
import de.be4.classicalb.core.parser.node.AImageExpression; //added
//import de.be4.classicalb.core.parser.node.APowSubsetExpression; //added
import de.be4.classicalb.core.parser.node.AImplicationPredicate; //added
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.AIntersectionExpression; //added
import de.be4.classicalb.core.parser.node.AIntervalExpression; //added
import de.be4.classicalb.core.parser.node.AIterationExpression;//added
import de.be4.classicalb.core.parser.node.ALessEqualPredicate;//added
import de.be4.classicalb.core.parser.node.ALessPredicate;//added
import de.be4.classicalb.core.parser.node.AMaxExpression;//added
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.AMinusOrSetSubtractExpression;
import de.be4.classicalb.core.parser.node.AModuloExpression;//added
import de.be4.classicalb.core.parser.node.AMultiplicationExpression;//added
import de.be4.classicalb.core.parser.node.ANat1SetExpression;
import de.be4.classicalb.core.parser.node.ANatSetExpression;
import de.be4.classicalb.core.parser.node.ANegationPredicate;//added
import de.be4.classicalb.core.parser.node.ANotEqualPredicate; //added
import de.be4.classicalb.core.parser.node.ANotMemberPredicate; //added
import de.be4.classicalb.core.parser.node.AOverwriteExpression; //added
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.APowerOfExpression;//added
import de.be4.classicalb.core.parser.node.ARangeExpression; //added
import de.be4.classicalb.core.parser.node.ARangeRestrictionExpression; //added
import de.be4.classicalb.core.parser.node.ARangeSubtractionExpression; //added
import de.be4.classicalb.core.parser.node.ARecEntry;
import de.be4.classicalb.core.parser.node.ARecExpression;
import de.be4.classicalb.core.parser.node.ARecordFieldExpression;
import de.be4.classicalb.core.parser.node.ARevExpression; //added
import de.be4.classicalb.core.parser.node.AReverseExpression; //added
import de.be4.classicalb.core.parser.node.ASeq1Expression;
import de.be4.classicalb.core.parser.node.ASequenceExtensionExpression; //added
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.ASizeExpression; //added
import de.be4.classicalb.core.parser.node.AStructExpression;
import de.be4.classicalb.core.parser.node.ASubsetPredicate;
import de.be4.classicalb.core.parser.node.ASubsetStrictPredicate; //added
import de.be4.classicalb.core.parser.node.ATailExpression; //added
import de.be4.classicalb.core.parser.node.AUnaryMinusExpression;//added
import de.be4.classicalb.core.parser.node.AUnionExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PRecEntry;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.TIntegerLiteral;
//added
//added -> ANegationPredicate
//added -> ADisjunctPredicate
//added -> ADomainRestrictionExpression
//added -> ADomainSubtractionExpression
//added -> ARangeSubtractionExpression
//added -> ACompositionExpression
//added -> AReverseExpression
//added -> AForallPredicate
//added -> AExistsPredicate
//added -> AEquivalencePeredicate
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//import de.be4.classicalb.core.parser.node.APowSubsetExpression; //added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added
//added

public class VdmToBConverter extends DepthFirstAnalysisAdaptorAnswer<Node>
{
	public static final String STATE_ID_PREFIX = "$";

	public static final String OLD_POST_FIX = "~";

	public static final String TOKEN_SET = "TOKEN";

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

	public VdmToBConverter()
	{
		constraints.add(new AMemberPredicate(getIdentifier(new LexNameToken("", TOKEN_SET, null)), new ANatSetExpression()));
		console = new SolverConsole();
	}

	public VdmToBConverter(SolverConsole console)
	{
		this();
		this.console = console;
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
	public Node caseASetCompSetExp(ASetCompSetExp node) // added
			throws AnalysisException
	{
		AComprehensionSetExpression scs = new AComprehensionSetExpression();

		LinkedList<PMultipleBind> blist = node.getBindings();
		scs.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		scs.setPredicates(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		// console.out.println("Setcompset: " + scs.getPredicates());

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
		// console.out.println("Setcompset: " + scs.getPredicates());
		// return scs;
		console.out.println("Setcompset: " + scs.getIdentifiers());
		console.out.println("Setcompset: " + scs.getPredicates());
		return new ARangeExpression(scs);
	}

	@Override
	public Node caseAIfExp(AIfExp node)// under construction
			throws AnalysisException
	{
		// node.getTest();//testpart
		// node.getThen();//thenpart
		ADisjunctPredicate dp = new ADisjunctPredicate();
		if (node.getElseList().size() == 0)
		{
			// console.out.println("ifExp: then " + node.getThen().getType());
			// console.out.println("ifExp: else " + node.getElse().getType());
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

	/*
	 * @Override public Node caseAElseIfExp(AElseIfExp node)//under construction throws AnalysisException {
	 * //node.getTest();//testpart //node.getThen();//thenpart //node.getElseIf();//elsepart return new
	 * ADisjointPredicate(new AConjunctPredicate(pred(node.getTest()), pred(node.getThen())), new AConjunctPredicate(new
	 * ANegationPredicate(pred(node.getTest())), pred(node.getElseIf()))); }
	 */

	@Override
	public Node caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
			throws AnalysisException
	{
		// System.out.println("in AndBooleanBinaryExp " + node);
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
		// System.out.println("result of getIdentifier " + getIdentifier(node.getName()));//added by his
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
		} else if (type instanceof ATokenBasicType)
		{
			return getIdentifier(new LexNameToken("", TOKEN_SET, null));
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
	public Node caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node)// added
			throws AnalysisException
	{
		return new ADisjunctPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotUnaryExp(ANotUnaryExp node)// added
			throws AnalysisException
	{
		return new ANegationPredicate(pred(node.getExp()));
	}

	@Override
	public Node caseABooleanConstExp(ABooleanConstExp node)// not yet check
			throws AnalysisException
	{
		// System.out.println("In booleanConst: " + node.getValue().getValue());
		System.out.println("new class: " + node.getValue());
		if (node.getValue().getValue())
		{
			// return new ATruthPredicate();
			return new ABooleanTrueExpression();
		} else
		{
			// return new AFalsityPredicate();
			return new ABooleanFalseExpression();
		}
	}

	@Override
	public Node caseAPlusNumericBinaryExp(APlusNumericBinaryExp node)// added
			throws AnalysisException
	{

		return new AAddExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node)// added
			throws AnalysisException
	{

		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseATimesNumericBinaryExp(ATimesNumericBinaryExp node)// added
			throws AnalysisException
	{

		return new AMultiplicationExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivideNumericBinaryExp(ADivideNumericBinaryExp node)// added
			throws AnalysisException
	{

		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivNumericBinaryExp(ADivNumericBinaryExp node)// added
			throws AnalysisException
	{
		// x div y = x / y
		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARemNumericBinaryExp(ARemNumericBinaryExp node)// added
			throws AnalysisException
	{
		// x rem y = x - y * (x/y)
		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), new AMultiplicationExpression(exp(node.getRight()), new ADivExpression(exp(node.getLeft()), exp(node.getRight()))));
	}

	@Override
	public Node caseAModNumericBinaryExp(AModNumericBinaryExp node)// added
			throws AnalysisException
	{

		return new AModuloExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node)// added
			throws AnalysisException
	{

		return new AUnaryMinusExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node)// added
			throws AnalysisException
	{

		PExp num = node.getExp();
		ASetExtensionExpression nums = new ASetExtensionExpression();
		nums.getExpressions().add(exp(num));
		nums.getExpressions().add(new AUnaryMinusExpression(exp(num)));
		return new AMaxExpression(nums);
	}

	@Override
	public Node caseAStarStarBinaryExp(AStarStarBinaryExp node)// added
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
	public Node caseALessNumericBinaryExp(ALessNumericBinaryExp node)// added
			throws AnalysisException
	{
		return new ALessPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node)// added
			throws AnalysisException
	{
		return new ALessEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node)// added
			throws AnalysisException
	{
		return new AGreaterPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node)// added
			throws AnalysisException
	{
		return new AGreaterEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASetIntersectBinaryExp(ASetIntersectBinaryExp node)// added
			throws AnalysisException
	{
		return new AIntersectionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADistUnionUnaryExp(ADistUnionUnaryExp node)// added
			throws AnalysisException
	{
		return new AGeneralUnionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseADistIntersectUnaryExp(ADistIntersectUnaryExp node)// added
			throws AnalysisException
	{
		return new AGeneralIntersectionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAPowerSetUnaryExp(APowerSetUnaryExp node)// added
			throws AnalysisException
	{
		return new APowSubsetExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node)// added
			throws AnalysisException
	{
		return new AImplicationPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotInSetBinaryExp(ANotInSetBinaryExp node)// added
			throws AnalysisException
	{
		return new ANotMemberPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node)// added
			throws AnalysisException
	{
		return new ASubsetStrictPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseANotEqualBinaryExp(ANotEqualBinaryExp node)// added
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
	public Node caseAHeadUnaryExp(AHeadUnaryExp node)// added
			throws AnalysisException
	{
		return new AFirstExpression(exp(node.getExp()));
	}

	@Override
	public Node caseATailUnaryExp(ATailUnaryExp node)// added
			throws AnalysisException
	{
		return new ATailExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAIndicesUnaryExp(AIndicesUnaryExp node)// added not yet check
			throws AnalysisException
	{
		LinkedList<PExp> seqmem = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		String size = new String(new Integer(seqmem.size()).toString());
		return new AIntervalExpression(new AIntegerExpression(new TIntegerLiteral("1")), new AIntegerExpression(new TIntegerLiteral(size)));
	}

	@Override
	public Node caseAElementsUnaryExp(AElementsUnaryExp node)// added
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
	public Node caseALenUnaryExp(ALenUnaryExp node)// added
			throws AnalysisException
	{
		return new ASizeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAReverseUnaryExp(AReverseUnaryExp node)// added not yet checked
			throws AnalysisException
	{
		return new ARevExpression(exp(node.getExp()));
	}

	@Override
	public Node caseASeqConcatBinaryExp(ASeqConcatBinaryExp node)// added
			throws AnalysisException
	{
		return new AConcatExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	// method for map
	@Override
	public Node caseAMapletExp(AMapletExp node)// added
			throws AnalysisException
	{
		ACoupleExpression cpl = new ACoupleExpression();
		cpl.getList().add(exp(node.getLeft()));
		cpl.getList().add(exp(node.getRight()));
		return cpl;
	}

	@Override
	public Node caseAMapEnumMapExp(AMapEnumMapExp node)// added
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression map = new ASetExtensionExpression();
		for (AMapletExp m : node.getMembers())
		{

			map.getExpressions().add((PExpression) caseAMapletExp(m));
		}

		return map;
	}

	@Override
	public Node caseAMapDomainUnaryExp(AMapDomainUnaryExp node)// added
			throws AnalysisException
	{
		// console.out.println("in MapDomainU: " + node.getExp());
		return new ADomainExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapRangeUnaryExp(AMapRangeUnaryExp node)// added
			throws AnalysisException
	{
		// console.out.println("in MapDomainU: " + node.getExp());
		return new ARangeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapUnionBinaryExp(AMapUnionBinaryExp node)// added
			throws AnalysisException
	{
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAPlusPlusBinaryExp(APlusPlusBinaryExp node)// added
			throws AnalysisException
	{
		// seq ++ map
		// map ++ map
		return new AOverwriteExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResToBinaryExp(ADomainResToBinaryExp node)// added
			throws AnalysisException
	{
		return new ADomainRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResByBinaryExp(ADomainResByBinaryExp node)// added
			throws AnalysisException
	{
		return new ADomainSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResToBinaryExp(ARangeResToBinaryExp node)// added
			throws AnalysisException
	{
		return new ARangeRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResByBinaryExp(ARangeResByBinaryExp node)// added
			throws AnalysisException
	{
		return new ARangeSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	/*
	 * @Override public Node caseAApplyExp(AApplyExp node)//added throws AnalysisException { AFunctionExpression fun =
	 * new AFunctionExpression(); fun.setIdentifier(exp(node.getRoot())); for(PExp m : node.getArgs()) {
	 * fun.getParameters().add(exp(m)); } return fun; //if(node.getType().equals("map")) { // ASetExtensionExpression
	 * mono = new ASetExtensionExpression(); // for(PExp m : node.getArgs()) { // mono.getExpressions().add(exp(m)); //
	 * } // return (Node)new AImageExpression(exp(node.getRoot()), mono); // } else if(node.getType().equals("seq")) {
	 * // AFunctionExpression fun = new AFunctionExpression(); // fun.setIdentifier(exp(node.getRoot())); // // for(PExp
	 * m : node.getArgs()) { // fun.getParameters().add(exp(m)); // } // return (Node)fun; // } }
	 */

	@Override
	public Node caseAApplyExp(AApplyExp node)// added
			throws AnalysisException
	{
		ASetExtensionExpression mono = new ASetExtensionExpression();
		for (PExp m : node.getArgs())
		{
			mono.getExpressions().add(exp(m));
		}
		return new AImageExpression(exp(node.getRoot()), mono);

	}

	@Override
	public Node caseACompBinaryExp(ACompBinaryExp node)// added
			throws AnalysisException
	{
		return new ACompositionExpression(exp(node.getRight()), exp(node.getLeft()));
	}

	@Override
	public Node caseAMapInverseUnaryExp(AMapInverseUnaryExp node)// added
			throws AnalysisException
	{
		return new AReverseExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAForAllExp(AForAllExp node)// added
			throws AnalysisException
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
					// console.out.println("forall: " + blist.get(i).getPlist().get(j));
					// console.out.println("forall: " + blist.get(i));
					fap.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					fap.setImplication(new AConjunctPredicate(fap.getImplication(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		fap.setImplication(new AImplicationPredicate(fap.getImplication(), pred(node.getPredicate())));

		return fap;
	}

	@Override
	public Node caseAExistsExp(AExistsExp node)// added
			throws AnalysisException
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
					console.out.println("exists: "
							+ blist.get(i).getPlist().get(j));
					console.out.println("exists: " + blist.get(i));
					esp.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), pred(node.getPredicate())));

		return esp;
	}

	@Override
	public Node caseAExists1Exp(AExists1Exp node)// added
			throws AnalysisException
	{
		// exists1 x in set S & pred -> card( { x | x : S & pred } ) = 1

		AIntegerExpression one = new AIntegerExpression(new TIntegerLiteral(new String(new Integer("1").toString())));
		AComprehensionSetExpression cse = new AComprehensionSetExpression();

		// System.out.println(((ASetBind)(node.getBind())).getSet());
		cse.setPredicates(new AMemberPredicate(exp(node.getBind().getPattern()), exp(((ASetBind) node.getBind()).getSet())));

		cse.getIdentifiers().add(exp(node.getBind().getPattern()));
		cse.setPredicates(new AConjunctPredicate(cse.getPredicates(), pred(node.getPredicate())));
		AEqualPredicate equal = new AEqualPredicate(new ACardExpression(cse), one);

		return equal;

	}

	@Override
	public Node caseAIdentifierPattern(AIdentifierPattern node)// added
			throws AnalysisException
	{
		AIdentifierExpression aie = new AIdentifierExpression();
		aie.getIdentifier().add(new TIdentifierLiteral(node.getName().toString()));
		return aie;
	}

	@Override
	public Node caseASetMultipleBind(ASetMultipleBind node)// added
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
	public Node caseAEquivalentBooleanBinaryExp(AEquivalentBooleanBinaryExp node)// added
			throws AnalysisException
	{
		return new AEquivalencePredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseADistConcatUnaryExp(ADistConcatUnaryExp node)// under construction
			throws AnalysisException
	{

		LinkedList<PExp> seqlist = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		if (seqlist.isEmpty())
		{
			return new AEmptySequenceExpression();
		}

		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();

		seq.getExpression().add(exp(seqlist.get(0)));
		LinkedList<PExp> temp = new LinkedList<PExp>();
		temp.add(seqlist.get(0));

		for (int i = 1; i < seqlist.size(); i++)
		{
			PExp m = seqlist.get(i);
			// System.err.println(m);
			// System.err.println(exp(m));
			if (temp.indexOf(m) == -1)
			{
				seq.getExpression().add(exp(m));
			}
		}
		return new AGeneralConcatExpression(seq);
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

		if ( node.getInitExpression() != null && USE_INITIAL_FIXED_STATE)
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
		if(node.getType() instanceof ATokenBasicType)
		{
		return node.getArg().apply(this);	
		}
		return super.caseAMkBasicExp(node);
	}
	
	
	/*types*/
	
	@Override
	public Node caseABooleanBasicType(ABooleanBasicType node)
			throws AnalysisException
	{
	return new ABoolSetExpression();
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
	public Node caseANatOneNumericBasicType(ANatOneNumericBasicType node) // added
			throws AnalysisException
	{
		return new ANat1SetExpression();
	}

	@Override
	public Node caseATokenBasicType(ATokenBasicType node)
			throws AnalysisException
	{
		return getIdentifier(new LexNameToken("", TOKEN_SET, null));
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
