/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.pog.obligation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexIntegerToken;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.pof.AVdmPoTree;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.POStatus;
import org.overture.pog.pub.POType;
import org.overture.pog.utility.GetLocationHelper;
import org.overture.pog.utility.PatternToExpVisitor;
import org.overture.pog.utility.UniqueNameGenerator;

/**
 * New class for Proof Obligatios with a an AST based representation (wip)
 * 
 * @author ldc
 */

// FIXME Old PO Extension and Comparable are placeholders to keep the POG
// compiling while we transition to the new style

abstract public class ProofObligation implements IProofObligation, Serializable {
	private static final long serialVersionUID = 1L;

	// we should store the node that generated the PO rather than just the
	// location
	public final INode rootNode;
	public final String name;

	// the value will now use the vdmpotree
	public AVdmPoTree valuetree;

	// will need to be changed once we bring in the TP from compass
	public POStatus status;

	// Not sure whether these will be still be needed
	public final POType kind;
	public int number;
	public POTrivialProof proof;
	private final UniqueNameGenerator generator;
	private ILexLocation location;

	public ProofObligation(INode rootNode, POType kind, IPOContextStack context) {
		this(rootNode, kind, context, GetLocationHelper.findLocation(rootNode));
	}

	public ProofObligation(INode rootnode, POType kind,
			IPOContextStack context, ILexLocation location) {
		this.rootNode = rootnode;
		this.location = location;
		this.kind = kind;
		this.name = context.getName();
		this.status = POStatus.UNPROVED;
		this.valuetree = new AVdmPoTree();
		this.generator = new UniqueNameGenerator(rootNode);
	}

	public ProofObligation(INode rootNode, String name, AVdmPoTree valuetree,
			POStatus status, POType kind, int number, POTrivialProof proof,
			int var) {
		super();
		this.rootNode = rootNode;
		this.location = GetLocationHelper.findLocation(rootNode);
		this.name = name;
		this.valuetree = valuetree;
		this.status = status;
		this.kind = kind;
		this.number = number;
		this.proof = proof;
		this.generator = new UniqueNameGenerator(rootNode);
	}

	public UniqueNameGenerator getUniqueGenerator() {
		return generator;
	}

	public AVdmPoTree getValueTree() {
		return valuetree;
	}

	// this method should call a visitor on the potree that creates the "value"
	// string as it exists in the current
	// version
	@Override
	public String getValue() {
		if (valuetree.getPredicate() == null) {
			return "";
		}
		String result = valuetree.getPredicate().toString();
		return result;
	}

	@Override
	public void setStatus(POStatus status) {
		this.status = status;

	}

	@Override
	public String toString() {
		return name + ": " + kind + " obligation " + "@ " + location + "\n"
				+ getValue();
	}
	
	

	@Override
	public INode getNode() {
		return rootNode;
	}

	// I'm not sure why the comparable is implemented...
	public int compareTo(IProofObligation other) {
		return number - other.getNumber();
	}

	@Override
	public POType getKind() {
		return kind;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public POStatus getStatus() {
		return status;
	}

	@Override
	public POTrivialProof getTrivialProof() {
		return proof;
	}

	@Override
	public void setNumber(int i) {
		number = i;

	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	public ILexLocation getLocation() {
		return location;
	}

	/**
	 * Create a multiple type bind with a varargs list of pattern variables,
	 * like a,b,c:T. This is used by several obligations.
	 */
	protected PMultipleBind getMultipleTypeBind(PType patternType,
			ILexNameToken... patternNames) {
		ATypeMultipleBind typeBind = new ATypeMultipleBind();
		List<PPattern> patternList = new Vector<PPattern>();

		for (ILexNameToken patternName : patternNames) {
			AIdentifierPattern pattern = new AIdentifierPattern();
			pattern.setName(patternName.clone());
			patternList.add(pattern);
		}

		typeBind.setPlist(patternList);
		typeBind.setType(patternType);

		return typeBind;
	}

	/**
	 * Create a multiple set bind with a varargs list of pattern variables, like
	 * a,b,c in set S. This is used by several obligations.
	 */
	protected PMultipleBind getMultipleSetBind(PExp setExp,
			ILexNameToken... patternNames) {
		ASetMultipleBind setBind = new ASetMultipleBind();
		List<PPattern> patternList = new Vector<PPattern>();

		for (ILexNameToken patternName : patternNames) {
			AIdentifierPattern pattern = new AIdentifierPattern();
			pattern.setName(patternName.clone());
			patternList.add(pattern);
		}

		setBind.setPlist(patternList);
		setBind.setSet(setExp);

		return setBind;
	}

	/**
	 * As above, but create a List<PMultipleBind> with one element, for
	 * convenience.
	 */
	protected List<PMultipleBind> getMultipleTypeBindList(PType patternType,
			ILexNameToken... patternNames) {
		List<PMultipleBind> typeBindList = new Vector<PMultipleBind>();
		typeBindList.add(getMultipleTypeBind(patternType, patternNames));
		return typeBindList;
	}

	/**
	 * As above, but create a List<PMultipleBind> with one element, for
	 * convenience.
	 */
	protected List<PMultipleBind> getMultipleSetBindList(PExp setExp,
			ILexNameToken... patternNames) {
		List<PMultipleBind> setBindList = new Vector<PMultipleBind>();
		setBindList.add(getMultipleSetBind(setExp, patternNames));
		return setBindList;
	}

	/**
	 * Create a LexNameToken with a numbered variable name, based on the stem
	 * passed. (See getVar above).
	 */
	protected ILexNameToken getUnique(String name) {
		return generator.getUnique(name);
	}

	/**
	 * Generate an AEqualsBinaryExp
	 */
	protected AEqualsBinaryExp getEqualsExp(PExp left, PExp right) {
		AEqualsBinaryExp equals = new AEqualsBinaryExp();
		equals.setLeft(left);
		equals.setOp(new LexKeywordToken(VDMToken.EQUALS, null));
		equals.setRight(right);
		return equals;
	}

	/**
	 * Generate an AVariableExp
	 */
	protected AVariableExp getVarExp(ILexNameToken name) {
		AVariableExp var = new AVariableExp();
		var.setName(name.clone());
		var.setOriginal(name.getFullName());
		return var;
	}

	/**
	 * Generate an AApplyExp with varargs arguments
	 */
	protected AApplyExp getApplyExp(PExp root, PExp... arglist) {
		return getApplyExp(root, Arrays.asList(arglist));
	}

	/**
	 * Generate an AApplyExp
	 */
	protected AApplyExp getApplyExp(PExp root, List<PExp> arglist) {
		AApplyExp apply = new AApplyExp();
		apply.setRoot(root.clone());
		List<PExp> args = new Vector<PExp>();

		for (PExp arg : arglist) {
			args.add(arg.clone());
		}

		apply.setArgs(args);
		return apply;
	}

	/**
	 * Generate an AIntLiteral from a long.
	 */
	protected AIntLiteralExp getIntLiteral(long i) {
		AIntLiteralExp number = new AIntLiteralExp();
		ILexIntegerToken literal = new LexIntegerToken(i, null);
		number.setValue(literal);
		return number;
	}

	/**
	 * Chain an AND expression onto a root, or just return the new expression if
	 * the root is null. Called in a loop, this left-associates an AND tree.
	 */
	protected PExp makeAnd(PExp root, PExp e) {
		if (root != null) {
			AAndBooleanBinaryExp a = new AAndBooleanBinaryExp();
			a.setLeft(root);
			a.setOp(new LexKeywordToken(VDMToken.AND, null));
			a.setType(new ABooleanBasicType());
			a.setRight(e);
			return a;
		} else {
			return e;
		}
	}

	/**
	 * Chain an OR expression onto a root, or just return the new expression if
	 * the root is null. Called in a loop, this left-associates an OR tree.
	 */
	protected PExp makeOr(PExp root, PExp e) {
		if (root != null) {
			AOrBooleanBinaryExp o = new AOrBooleanBinaryExp();
			o.setLeft(root);
			o.setOp(new LexKeywordToken(VDMToken.OR, null));
			o.setType(new ABooleanBasicType());
			o.setRight(e);
			return o;
		} else {
			return e;
		}
	}

	/**
	 * Create an expression equivalent to a pattern.
	 */
	protected PExp patternToExp(PPattern pattern) throws AnalysisException {
		PatternToExpVisitor visitor = new PatternToExpVisitor(
				getUniqueGenerator());
		return pattern.apply(visitor);
	}
}
