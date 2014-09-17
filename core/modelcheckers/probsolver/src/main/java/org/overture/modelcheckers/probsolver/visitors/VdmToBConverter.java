/*
 * #%~
 * Integration of the ProB Solver for VDM
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.modelcheckers.probsolver.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
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

import de.be4.classicalb.core.parser.node.ABoolSetExpression;
import de.be4.classicalb.core.parser.node.AComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;
import de.be4.classicalb.core.parser.node.ADomainExpression;
import de.be4.classicalb.core.parser.node.AEmptySequenceExpression;
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AIntSetExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.ANat1SetExpression;
import de.be4.classicalb.core.parser.node.ANatSetExpression;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.ARangeExpression;
import de.be4.classicalb.core.parser.node.ARecEntry;
import de.be4.classicalb.core.parser.node.ARecExpression;
import de.be4.classicalb.core.parser.node.ARecordFieldExpression;
import de.be4.classicalb.core.parser.node.ARelationsExpression;
import de.be4.classicalb.core.parser.node.ASeq1Expression;
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.AStringExpression;
import de.be4.classicalb.core.parser.node.AStringSetExpression;
import de.be4.classicalb.core.parser.node.AStructExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PRecEntry;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;

public class VdmToBConverter extends VdmToBExpressionConverter
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
			scs.setPredicates(new AConjunctPredicate(scs.getPredicates(), new AMemberPredicate(exp(blist.get(0).getPlist().get(j)), exp(blist.get(0)))));
		}

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
	protected PExpression getIdentifier(String n)
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
	public Node caseAElementsUnaryExp(AElementsUnaryExp node)
			throws AnalysisException
	{
		// System.err.println("in caseAElementsUnaryExp node: " + node);
		// System.err.println("in caseAElementsUnaryExp node.getExp(): "
		// + node.getExp());
		// System.err.println("in caseAElementsUnaryExp node.getExp().apply(this): "
		// + node.getExp().apply(this));

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

	/*
	 * @Override public Node caseASeqCompSeqExp(ASeqCompSeqExp node) throws AnalysisException { //[e | id in set S & P]
	 * // create { id, _target_ | id:S & P & _target_-e } System.err.println("first: " + node.getFirst()); //e
	 * System.err.println("pattern: " + (node.getSetBind()).getPattern()); //id System.err.println("set: " +
	 * (node.getSetBind()).getSet()); //S System.err.println("pred: " + node.getPredicate()); //P
	 * AComprehensionSetExpression scs = new AComprehensionSetExpression();
	 * scs.getIdentifiers().add(exp(node.getSetBind().getPattern())); scs.setPredicates(new
	 * AMemberPredicate(exp(node.getSetBind().getPattern()), exp(node.getSetBind().getSet()))); scs.setPredicates(new
	 * AConjunctPredicate(scs.getPredicates(), pred(node.getPredicate()))); List<TIdentifierLiteral> ident = new
	 * ArrayList<TIdentifierLiteral>(); ident.add(new TIdentifierLiteral("_target_")); PExpression temp = new
	 * AIdentifierExpression(ident); scs.getIdentifiers().add(temp); scs.setPredicates(new
	 * AConjunctPredicate(scs.getPredicates(), new AEqualPredicate(temp, exp(node.getFirst()))));
	 * if(scs.getMembers().isEmpty()) { AEmptySequeneExpression seq = new AEmptySequeneExpression(); } else {
	 * ASequenceExtensionExpression seq = new ASequenceExtensionExpression(); for(PExpression elem : scs.getMembers()) {
	 * seq.add(elem.getList().getLast()); } } return seq; LinkedList<PExp> seqlist = ((ASeqEnumSeqExp)
	 * node.getExp()).getMembers(); if (seqlist.isEmpty()) { return new AEmptySequenceExpression(); }
	 * ASequenceExtensionExpression seq = new ASequenceExtensionExpression(); LinkedList<PExp> temp = new
	 * LinkedList<PExp>(); for (PExp m : seqlist) { seq.getExpression().add(exp(m)); } return new
	 * AGeneralConcatExpression(seq); }************
	 */

	// method for map

	@Override
	public Node caseAMapCompMapExp(AMapCompMapExp node)
			throws AnalysisException
	{
		int domtimes = 0;
		AComprehensionSetExpression mapcomp = new AComprehensionSetExpression();

		AMapletExp maplet = node.getFirst();
		List<PMultipleBind> bindings = node.getBindings();

		// {a|->a+1 | a in set {1,2,3] & true } --> {from, to | a : {1,2,3} & true & _from = a & _to_ = a+1 }
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

		System.err.println("length of bindings: "
				+ bindings.get(0).getPlist().size());

		for (int j = 1; j < bindings.get(0).getPlist().size(); j++)
		{
			domtimes++;
			mapcomp.getIdentifiers().add(exp(bindings.get(0).getPlist().get(j)));
			mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AMemberPredicate(exp(bindings.get(0).getPlist().get(j)), exp(bindings.get(0)))));
		}

		for (int i = 1; i < bindings.size(); i++)
		{
			for (int j = 0; j < bindings.get(i).getPlist().size(); j++)
			{
				domtimes++;
				mapcomp.getIdentifiers().add(exp(bindings.get(i).getPlist().get(j)));
				mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AMemberPredicate(exp(bindings.get(i).getPlist().get(j)), exp(bindings.get(i)))));
			}
		}

		if (node.getPredicate() != null)
		{
			mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), pred(node.getPredicate())));
		}

		mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(from, exp(maplet.getLeft()))));
		mapcomp.setPredicates(new AConjunctPredicate(mapcomp.getPredicates(), new AEqualPredicate(to, exp(maplet.getRight()))));

		ADomainExpression domexp = new ADomainExpression(mapcomp);

		for (int i = 0; i < domtimes; i++)
		{
			domexp = new ADomainExpression(domexp);
		}
		return domexp;
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
			if (node.getInitExpression() instanceof AEqualsBinaryExp)
			{
				PExpression right = (PExpression) ((AEqualsBinaryExp) node.getInitExpression()).getRight().apply(this);
				AEqualPredicate init = new AEqualPredicate(getIdentifier(nameOld), right);
				p = new AConjunctPredicate(p, init);
			} else
			{
				// FIXME: unsupported expression

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

			nameSubLocal = extractRecordInvNameMapping(node.getInvdef().getParamPatternList(), node.getFields());

			for (Entry<String, String> names : nameSubLocal.entrySet())
			{
				nameSubLocalOld.put(names.getKey(), names.getValue()
						+ OLD_POST_FIX);
			}

			Node inv = applyWithSubstitution(nameSubLocal, node.getInvExpression());
			Node invOld = applyWithSubstitution(nameSubLocalOld, node.getInvExpression());
			PPredicate invs = new AConjunctPredicate((PPredicate) inv, (PPredicate) invOld);
			p = new AConjunctPredicate(p, invs);
		}

		return p;
	}

	private Map<String, String> extractRecordInvNameMapping(
			List<List<PPattern>> patternList, LinkedList<AFieldField> fields)
	{
		Map<String, String> nameSubLocal = new HashMap<String, String>();
		for (List<PPattern> plist : patternList)
		{
			for (PPattern pr : plist)
			{
				if (pr instanceof ARecordPattern)
				{
					ARecordPattern record = (ARecordPattern) pr;
					Iterator<PPattern> itrPattern = record.getPlist().iterator();
					Iterator<AFieldField> itrField = fields.iterator();
					while (itrPattern.hasNext())
					{
						String thisP = itrPattern.next().toString();
						String thisF = itrField.next().getTagname().getName();
						nameSubLocal.put(thisP, thisF);
					}
				}
			}
		}
		return nameSubLocal;
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

		if (USE_INITIAL_FIXED_STATE && node.getInitialized())
		{
			AEqualPredicate init = new AEqualPredicate(getIdentifier(nameOld), exp(node.getExpression()));
			p = new AConjunctPredicate(p, init);
		}

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

		Set<ILexNameToken> constants = new HashSet<ILexNameToken>(StateNameCollector.collectStateNamesFromOwningDefinition(node));

		// remove all state names thats mentioned in a write frame condition
		for (AExternalClause frameClause : node.getExternals())
		{
			if (frameClause.getMode().is(VDMToken.WRITE))
			{
				for (ILexNameToken rwIds : frameClause.getIdentifiers())
				{
					Set<ILexNameToken> remove = new HashSet<ILexNameToken>();
					for (ILexNameToken id : constants)
					{
						if (id.getName().equals(rwIds.getName()))
						{
							remove.add(id);
							break;
						}
					}
					constants.removeAll(remove);
				}
			}
		}

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
		Map<String, String> noRecursion = new HashMap<String, String>();
		noRecursion.putAll(substitution);

		// remove all a->a pairs since this give an infinit recursive lookup
		for (Entry<String, String> entry : substitution.entrySet())
		{
			if (entry.getKey().equals(entry.getValue()))
			{
				noRecursion.remove(entry.getKey());
			}
		}

		nameSubstitution.putAll(noRecursion);

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
	public Node caseAQuoteLiteralExp(AQuoteLiteralExp node)
			throws AnalysisException
	{
		return createIdentifier(getQuoteLiteralName(node.getValue().getValue()));
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
		AComprehensionSetExpression scs = new AComprehensionSetExpression();

		List<PExpression> identifiers = new Vector<PExpression>();
		AExplicitFunctionDefinition invDef = node.getInvDef();
		if (invDef == null)
		{
			return node.getType().apply(this);
		}

		for (List<PPattern> pList : invDef.getParamPatternList())
		{
			for (PPattern pPattern : pList)
			{
				if (pPattern instanceof AIdentifierPattern)
				{
					AIdentifierPattern ip = (AIdentifierPattern) pPattern;
					identifiers.add(getIdentifier(ip.getName()));
				}
			}
		}
		scs.setIdentifiers(identifiers);

		PPredicate invPred = pred(invDef.getBody());

		AMemberPredicate eqp = new AMemberPredicate((PExpression) identifiers.get(0).clone(), (PExpression) node.getType().apply(this));

		AConjunctPredicate predicate = new AConjunctPredicate(invPred, eqp);

		scs.setPredicates(predicate);
		return scs;
	}

	@Override
	public Node caseARecordInvariantType(ARecordInvariantType node)
			throws AnalysisException
	{
		AExplicitFunctionDefinition invDef = node.getInvDef();

		AStructExpression struct = new AStructExpression(getEntities(node.getFields()));
		if (invDef == null)
		{
			return struct;
		}

		AComprehensionSetExpression scs = new AComprehensionSetExpression();

		List<PExpression> identifiers = new Vector<PExpression>();

		identifiers.add(getIdentifier("£"));

		scs.setIdentifiers(identifiers);

		Map<String, String> nameMapping = extractRecordInvNameMapping(invDef.getParamPatternList(), node.getFields());

		PPredicate invPred = (PPredicate) applyWithSubstitution(nameMapping, invDef.getBody());

		AMemberPredicate eqp = new AMemberPredicate((PExpression) identifiers.get(0).clone(), struct);

		AConjunctPredicate predicate = new AConjunctPredicate(invPred, eqp);
		
		for (String field : nameMapping.keySet())
		{
			AEqualPredicate eq = new AEqualPredicate(getIdentifier(field), new ARecordFieldExpression(getIdentifier("£"),getIdentifier(field)));
			predicate = new AConjunctPredicate(predicate,eq);
		}

		scs.setPredicates(predicate);

		return scs;
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
	 * @Override public Node caseAProductType(AProductType node) throws AnalysisException { return }
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
