/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.definitions;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.PostOpExpression;
import org.overturetool.vdmj.expressions.PreOpExpression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.pog.POOperationDefinitionContext;
import org.overturetool.vdmj.pog.SatisfiabilityObligation;
import org.overturetool.vdmj.pog.OperationPostConditionObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ParameterPatternObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.StateInvariantObligation;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.ErrorCase;
import org.overturetool.vdmj.statements.ExternalClause;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
import org.overturetool.vdmj.statements.SpecificationStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.PatternTypePair;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.OperationValue;


/**
 * A class to hold an explicit operation definition.
 */

public class ImplicitOperationDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final List<PatternListTypePair> parameterPatterns;
	public final PatternTypePair result;
	public final List<ExternalClause> externals;
	public final Statement body;
	public final Expression precondition;
	public final Expression postcondition;
	public final List<ErrorCase> errors;

	public OperationType type;		// Created from params/result
	public ExplicitFunctionDefinition predef;
	public ExplicitFunctionDefinition postdef;
	public StateDefinition state;
	public Type actualResult;
	private Definition stateDefinition = null;
	public boolean isConstructor = false;

	public ImplicitOperationDefinition(LexNameToken name,
		List<PatternListTypePair> parameterPatterns,
		PatternTypePair result, Statement body,
		SpecificationStatement spec)
	{
		super(Pass.DEFS, name.location, name, NameScope.GLOBAL);

		this.parameterPatterns = parameterPatterns;
		this.result = result;
		this.body = body;
		this.externals = spec.externals;
		this.precondition = spec.precondition;
		this.postcondition = spec.postcondition;
		this.errors = spec.errors;

		TypeList ptypes = new TypeList();

		for (PatternListTypePair ptp: parameterPatterns)
		{
			ptypes.addAll(ptp.getTypeList());
		}

		type = new OperationType(location, ptypes,
					(result == null ? new VoidType(name.location) : result.type));
	}

	@Override
	public String toString()
	{
		return	name + Utils.listToString("(", parameterPatterns, ", ", ")") +
				(result == null ? "" : " " + result) +
				(externals == null ? "" : "\n\text " + externals) +
				(precondition == null ? "" : "\n\tpre " + precondition) +
				(postcondition == null ? "" : "\n\tpost " + postcondition) +
				(errors == null ? "" : "\n\terrs " + errors);
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		state = base.findStateDefinition();

		if (precondition != null)
		{
			predef = getPreDefinition(base);
			predef.markUsed();
		}

		if (postcondition != null)
		{
			postdef = getPostDefinition(base);
			postdef.markUsed();
		}
	}

	@Override
	public void typeResolve(Environment base)
	{
		type = type.typeResolve(base, null);

		if (result != null)
		{
			result.typeResolve(base);
		}

		if (base.isVDMPP())
		{
			name.setTypeQualifier(type.parameters);

			if (body instanceof SubclassResponsibilityStatement)
			{
				classDefinition.isAbstract = true;
			}
		}

		if (precondition != null)
		{
			predef.typeResolve(base);
		}

		if (postcondition != null)
		{
			postdef.typeResolve(base);
		}

		for (PatternListTypePair ptp: parameterPatterns)
		{
			ptp.typeResolve(base);
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		scope = NameScope.NAMESANDSTATE;
		DefinitionList defs = new DefinitionList();
		DefinitionSet argdefs = new DefinitionSet();

		if (base.isVDMPP())
		{
			stateDefinition = base.findClassDefinition();
		}
		else
		{
			stateDefinition = base.findStateDefinition();
		}

		for (PatternListTypePair ptp: parameterPatterns)
		{
			argdefs.addAll(ptp.getDefinitions(NameScope.LOCAL));
		}

		defs.addAll(argdefs.asList());

		if (result != null)
		{
			defs.addAll(
				result.pattern.getDefinitions(type.result, NameScope.LOCAL));
		}

		// Now we build local definitions for each of the externals, so
		// that they can be added to the local environment, while the
		// global state is made inaccessible.

		if (externals != null)
		{
    		for (ExternalClause clause: externals)
    		{
    			for (LexNameToken exname: clause.identifiers)
    			{
    				Definition sdef = base.findName(exname, NameScope.STATE);
    				clause.typeResolve(base);

    				if (sdef == null)
    				{
    					report(3031, "Unknown state variable " + exname);
    				}
    				else
    				{
    					if (!(clause.type instanceof UnknownType) &&
    						!sdef.getType().equals(clause.type))
        				{
        					report(3032, "State variable " + exname + " is not this type");
        					detail2("Declared", sdef.getType(), "ext type", clause.type);
        				}
        				else
        				{
            				defs.add(new ExternalDefinition(sdef, clause.mode));

            				// VDM++ "ext wr" clauses in a constructor effectively
            				// initialize the instance variable concerned.

            				if (clause.mode.is(Token.WRITE) &&
            					sdef instanceof InstanceVariableDefinition &&
            					name.name.equals(classDefinition.name.name))
            				{
            					InstanceVariableDefinition iv = (InstanceVariableDefinition)sdef;
            					iv.initialized = true;
            				}
        				}
    				}
    			}
    		}
		}

		defs.typeCheck(base, scope);
		FlatCheckedEnvironment local = new FlatCheckedEnvironment(defs, base, scope);
		local.setStatic(accessSpecifier);
		local.setEnclosingDefinition(this);

		if (body != null)
		{
			if (classDefinition != null && !accessSpecifier.isStatic)
			{
				local.add(getSelfDefinition());
			}

			if (base.isVDMPP())
			{
				if (name.name.equals(classDefinition.name.name))
    			{
    				isConstructor = true;
    				classDefinition.hasConstructors = true;

    				if (accessSpecifier.isAsync)
    				{
    					report(3286, "Constructor cannot be 'async'");
    				}

    				if (type.result.isClass())
    				{
    					ClassType ctype = type.result.getClassType();

    					if (ctype.classdef != classDefinition)
    					{
    						type.result.report(3025,
    							"Constructor operation must have return type " + classDefinition.name.name);
    					}
    				}
    				else
    				{
    					type.result.report(3026,
    						"Constructor operation must have return type " + classDefinition.name.name);
    				}
    			}
			}

			actualResult = body.typeCheck(local, NameScope.NAMESANDSTATE);
			boolean compatible = TypeComparator.compatible(type.result, actualResult);

			if ((isConstructor && !actualResult.isType(VoidType.class) && !compatible) ||
				(!isConstructor && !compatible))
			{
				report(3035, "Operation returns unexpected type");
				detail2("Actual", actualResult, "Expected", type.result);
			}
		}

		if (accessSpecifier.isAsync && !type.result.isType(VoidType.class))
		{
			report(3293, "Asynchronous operation " + name + " cannot return a value");
		}

		if (type.narrowerThan(accessSpecifier))
		{
			report(3036, "Operation type narrows operation");
		}

		if (predef != null)
		{
			Type b = predef.body.typeCheck(local, null, NameScope.NAMESANDSTATE);
			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Precondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		// The result variables are in scope for the post condition

		if (postdef != null)
		{
			Type b = null;

			if (result != null)
			{
	    		DefinitionList postdefs = result.getDefinitions();
	    		FlatCheckedEnvironment post =
	    			new FlatCheckedEnvironment(postdefs, local, NameScope.NAMESANDANYSTATE);
	    		post.setStatic(accessSpecifier);
				b = postdef.body.typeCheck(post, null, NameScope.NAMESANDANYSTATE);
				post.unusedCheck();
			}
			else
			{
				b = postdef.body.typeCheck(local, null, NameScope.NAMESANDANYSTATE);
			}

			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Postcondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		if (!(body instanceof NotYetSpecifiedStatement) &&
			!(body instanceof SubclassResponsibilityStatement))
		{
			local.unusedCheck();
		}
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope incState)
	{
		if (super.findName(sought, incState) != null)
		{
			return this;
		}

		if (predef != null && predef.findName(sought, incState) != null)
		{
			return predef;
		}

		if (postdef != null && postdef.findName(sought, incState) != null)
		{
			return postdef;
		}

		return null;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		if (predef != null)
		{
			Expression found = predef.findExpression(lineno);
			if (found != null) return found;
		}

		if (postdef != null)
		{
			Expression found = postdef.findExpression(lineno);
			if (found != null) return found;
		}

		return body == null ? null : body.findExpression(lineno);
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return body == null ? null : body.findStatement(lineno);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc =
			(predef == null) ? null : new FunctionValue(predef, null, null, null);

		FunctionValue postfunc =
			(postdef == null) ? null : new FunctionValue(postdef, null, null, null);

		// Note, body may be null if it is really implicit. This is caught
		// when the function is invoked. The value is needed to implement
		// the pre_() expression for implicit functions.

		OperationValue op =	new OperationValue(this, prefunc, postfunc, state);
		op.isConstructor = isConstructor;
		op.isStatic = accessSpecifier.isStatic;
		nvl.add(new NameValuePair(name, op));

		if (predef != null)
		{
			prefunc.isStatic = accessSpecifier.isStatic;
			nvl.add(new NameValuePair(predef.name, prefunc));
		}

		if (postdef != null)
		{
			postfunc.isStatic = accessSpecifier.isStatic;
			nvl.add(new NameValuePair(postdef.name, postfunc));
		}

		return nvl;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		DefinitionList defs = new DefinitionList(this);

		if (predef != null)
		{
			defs.add(predef);
		}

		if (postdef != null)
		{
			defs.add(postdef);
		}

		return defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		return new LexNameList(name);
	}

	public PatternList getParamPatternList()
	{
		PatternList plist = new PatternList();

		for (PatternListTypePair pl: parameterPatterns)
		{
			plist.addAll(pl.patterns);
		}

		return plist;
	}

	public List<PatternList> getListParamPatternList()
	{
		List<PatternList> list = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (PatternListTypePair pl: parameterPatterns)
		{
			plist.addAll(pl.patterns);
		}

		list.add(plist);
		return list;
	}

	private ExplicitFunctionDefinition getPreDefinition(Environment base)
	{
		List<PatternList> parameters = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (PatternListTypePair pl: parameterPatterns)
		{
			plist.addAll(pl.patterns);
		}

		if (state != null)
		{
			plist.add(new IdentifierPattern(state.name));
		}
		else if (base.isVDMPP() && !accessSpecifier.isStatic)
		{
			plist.add(new IdentifierPattern(name.getSelfName()));
		}

		parameters.add(plist);
		PreOpExpression preop = new PreOpExpression(name, precondition, state);

		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getPreName(precondition.location), NameScope.GLOBAL,
			null, type.getPreType(state, classDefinition, accessSpecifier.isStatic),
			parameters, preop, null, null, false, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccessSpecifier(accessSpecifier.getStatic(false));
		def.classDefinition = classDefinition;
		return def;
	}

	private ExplicitFunctionDefinition getPostDefinition(Environment base)
	{
		List<PatternList> parameters = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (PatternListTypePair pl: parameterPatterns)
		{
			plist.addAll(pl.patterns);
		}

		if (result != null)
		{
			plist.add(result.pattern);
		}

		if (state != null)
		{
			plist.add(new IdentifierPattern(state.name.getOldName()));
			plist.add(new IdentifierPattern(state.name));
		}
		else if (base.isVDMPP() && !accessSpecifier.isStatic)
		{
			plist.add(new IdentifierPattern(name.getSelfName().getOldName()));
			plist.add(new IdentifierPattern(name.getSelfName()));
		}

		parameters.add(plist);
		PostOpExpression postop = new PostOpExpression(name, postcondition, state);

		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getPostName(postcondition.location), NameScope.GLOBAL,
			null, type.getPostType(state, classDefinition, accessSpecifier.isStatic),
			parameters, postop, null, null, false, null);

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccessSpecifier(accessSpecifier.getStatic(false));
		def.classDefinition = classDefinition;
		return def;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		for (Pattern p: getParamPatternList())
		{
			pids.addAll(p.getVariableNames());
		}

		if (pids.hasDuplicates())
		{
			obligations.add(new ParameterPatternObligation(this, ctxt));
		}

		if (precondition != null)
		{
			obligations.addAll(precondition.getProofObligations(ctxt));
		}

		if (postcondition != null)
		{
			obligations.addAll(postcondition.getProofObligations(ctxt));
			obligations.add(new OperationPostConditionObligation(this, ctxt));
		}

		if (body != null)
		{
			obligations.addAll(body.getProofObligations(ctxt));

			if (isConstructor &&
				classDefinition != null &&
				classDefinition.invariant != null)
			{
				obligations.add(new StateInvariantObligation(this, ctxt));
			}

			if (!isConstructor &&
				!TypeComparator.isSubType(actualResult, type.result))
			{
				obligations.add(
					new SubTypeObligation(this, actualResult, ctxt));
			}
		}
		else
		{
			if (postcondition != null)
			{
				ctxt.push(new POOperationDefinitionContext(this, false, stateDefinition));
				obligations.add(
					new SatisfiabilityObligation(this, stateDefinition, ctxt));
				ctxt.pop();
			}
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "implicit operation";
	}

	@Override
	public boolean isOperation()
	{
		return true;
	}

	@Override
	public boolean isCallableOperation()
	{
		return (body != null);
	}
}
