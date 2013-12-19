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
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.modelcheckers.probsolver.SolverConsole;

import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.AMinusOrSetSubtractExpression;
import de.be4.classicalb.core.parser.node.ANatSetExpression;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.ARecEntry;
import de.be4.classicalb.core.parser.node.ARecordFieldExpression;
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.AStructExpression;
import de.be4.classicalb.core.parser.node.ASubsetPredicate;
import de.be4.classicalb.core.parser.node.AUnionExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PRecEntry;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.TIntegerLiteral;

public class VdmToBConverter extends DepthFirstAnalysisAdaptorAnswer<Node>
{
	public static final String TOKEN_SET = "TOKEN";

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
		return (PExpression) n.apply(this);
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
		return (PPredicate) n.apply(this);
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
	/*
	@Override
	public Node caseASetCompSetExp(ASeCompSetExp node)
			throws AnalysisException
	{
	    node.getFirst();// target, variables
	    node.getBindings(); //blist = for(PExp m : node.getBindings() ) { blist+=(exp(m) + "&")
	    node.getPredicate(); // pred = exp(node.getPredicate()) + "target = exp(node.getFirst()));
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
	*/
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
		//System.out.println("result of getIdentifier " + getIdentifier(node.getName()));//added by his
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
			n += "~";
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
		String name = "$" + node.getName().getName().toLowerCase();
		if (old)
		{
			name += "~";
		}
		return name;

	}

	public static LexNameToken getStateIdToken(PDefinition node, boolean old)
	{
		String name = "$" + node.getName().getName().toLowerCase();
		if (old)
		{
			name += "~";
		}
		return new LexNameToken("", name, node.getLocation());

	}

	@Override
	public Node caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		String name = getStateId(node, false);
		String nameOld = getStateId(node, true);

		PPredicate before = new AMemberPredicate(getIdentifier(nameOld), new AStructExpression(getEntities(node.getFields())));
		PPredicate after = new AMemberPredicate(getIdentifier(name), new AStructExpression(getEntities(node.getFields())));
		PPredicate p = new AConjunctPredicate(before, after);

		for (AFieldField f : node.getFields())
		{
			nameSubstitution.put(f.getTagname().getName() + "~", nameOld + "'"
					+ f.getTagname().getName());
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
							nameSubLocalOld.put(thisP, thisF + "~");
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

		for (ILexNameToken id : constants)
		{
			PPredicate conjoin = new AEqualPredicate(getIdentifier(id), getIdentifier(id.getName()
					+ "~"));

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
	public Node caseASetType(ASetType node) throws AnalysisException
	{
		return new APowSubsetExpression(exp(node.getSetof()));
	}

	@Override
	public Node caseANamedInvariantType(ANamedInvariantType node)
			throws AnalysisException
	{
		// handle inv
		return node.getType().apply(this);

	}

	@Override
	public Node caseANatNumericBasicType(ANatNumericBasicType node)
			throws AnalysisException
	{
		return new ANatSetExpression();
	}

	@Override
	public Node caseATokenBasicType(ATokenBasicType node)
			throws AnalysisException
	{
		return getIdentifier(new LexNameToken("", TOKEN_SET, null));
	}

	@Override
	public Node caseAIntLiteralExp(AIntLiteralExp node)
			throws AnalysisException
	{
		return new AIntegerExpression(new TIntegerLiteral(""
				+ node.getValue().getValue()));
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
