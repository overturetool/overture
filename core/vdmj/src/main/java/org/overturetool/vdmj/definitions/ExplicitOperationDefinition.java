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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.PostOpExpression;
import org.overturetool.vdmj.expressions.PreOpExpression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.pog.ParameterPatternObligation;
import org.overturetool.vdmj.pog.OperationPostConditionObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
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
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.OperationValue;


/**
 * A class to hold an explicit operation definition.
 */

public class ExplicitOperationDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public OperationType type;
	public final PatternList parameterPatterns;
	public final Expression precondition;
	public final Expression postcondition;
	public final Statement body;

	public ExplicitFunctionDefinition predef;
	public ExplicitFunctionDefinition postdef;
	public DefinitionList paramDefinitions;
	public StateDefinition state;

	private Type actualResult = null;
	public boolean isConstructor = false;

	public ExplicitOperationDefinition(LexNameToken name, OperationType type,
		PatternList parameters, Expression precondition,
		Expression postcondition, Statement body)
	{
		super(Pass.DEFS, name.location, name, NameScope.GLOBAL);

		this.type = type;
		this.parameterPatterns = parameters;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.body = body;
	}

	@Override
	public String toString()
	{
		return  name + " " + type +
				"\n\t" + name + "(" + Utils.listToString(parameterPatterns) + ")" +
				(body == null ? "" : " ==\n" + body) +
				(precondition == null ? "" : "\n\tpre " + precondition) +
				(postcondition == null ? "" : "\n\tpost " + postcondition);
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

		for (Pattern p: parameterPatterns)
		{
			p.typeResolve(base);
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		scope = NameScope.NAMESANDSTATE;
		TypeList ptypes = type.parameters;

		if (parameterPatterns.size() > ptypes.size())
		{
			report(3023, "Too many parameter patterns");
			detail2("Type params", ptypes.size(),
				"Patterns", parameterPatterns.size());
			return;
		}
		else if (parameterPatterns.size() < ptypes.size())
		{
			report(3024, "Too few parameter patterns");
			detail2("Type params", ptypes.size(),
				"Patterns", parameterPatterns.size());
			return;
		}

		paramDefinitions = getParamDefinitions();
		paramDefinitions.typeCheck(base, scope);

		FlatCheckedEnvironment local =
			new FlatCheckedEnvironment(paramDefinitions, base, scope);
		local.setStatic(accessSpecifier);
		local.setEnclosingDefinition(this);

		if (base.isVDMPP())
		{
			if (!accessSpecifier.isStatic)
			{
				local.add(getSelfDefinition());
			}

			if (name.name.equals(classDefinition.name.name))
			{
				isConstructor = true;

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

		if (postdef != null)
		{
			LexNameToken result = new LexNameToken(name.module, "RESULT", location);
			Pattern rp = new IdentifierPattern(result);
			DefinitionList rdefs = rp.getDefinitions(type.result, NameScope.NAMESANDANYSTATE);
			FlatCheckedEnvironment post =
				new FlatCheckedEnvironment(rdefs, local, NameScope.NAMESANDANYSTATE);

			Type b = postdef.body.typeCheck(post, null, NameScope.NAMESANDANYSTATE);
			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Postcondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		actualResult = body.typeCheck(local, NameScope.NAMESANDSTATE);
		boolean compatible = TypeComparator.compatible(type.result, actualResult);

		if ((isConstructor && !actualResult.isType(VoidType.class) && !compatible) ||
			(!isConstructor && !compatible))
		{
			report(3027, "Operation returns unexpected type");
			detail2("Actual", actualResult, "Expected", type.result);
		}

		if (accessSpecifier.isAsync && !type.result.isType(VoidType.class))
		{
			report(3293, "Asynchronous operation " + name + " cannot return a value");
		}

		if (type.narrowerThan(accessSpecifier))
		{
			report(3028, "Operation type narrows operation");
		}

		if (!(body instanceof NotYetSpecifiedStatement))
		{
			local.unusedCheck();
		}
	}

	@Override
	public Type getType()
	{
		return type;		// NB entire "==>" type, not result
	}

	private DefinitionList getParamDefinitions()
	{
		DefinitionSet defs = new DefinitionSet();
		Iterator<Type> titer = type.parameters.iterator();

		for (Pattern p: parameterPatterns)
		{
   			defs.addAll(p.getDefinitions(titer.next(), NameScope.LOCAL));
		}

		return defs.asList();
	}

	public List<PatternList> getParamPatternList()
	{
		List<PatternList> parameters = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (Pattern p: parameterPatterns)
		{
			plist.add(p);
		}

		parameters.add(plist);
		return parameters;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		if (super.findName(sought, scope) != null)
		{
			return this;
		}

		if (predef != null && predef.findName(sought, scope) != null)
		{
			return predef;
		}

		if (postdef != null && postdef.findName(sought, scope) != null)
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

		return body.findExpression(lineno);
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return body.findStatement(lineno);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc =
			(predef == null) ? null : new FunctionValue(predef, null, null, null);

		FunctionValue postfunc =
			(postdef == null) ? null : new FunctionValue(postdef, null, null, null);

		OperationValue op = new OperationValue(this, prefunc, postfunc, state);
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

	private ExplicitFunctionDefinition getPreDefinition(Environment base)
	{
		List<PatternList> parameters = new Vector<PatternList>();
		PatternList plist = new PatternList();
		plist.addAll(parameterPatterns);

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
		plist.addAll(parameterPatterns);

		if (!(type.result instanceof VoidType))
		{
    		LexNameToken result =
    			new LexNameToken(name.module, "RESULT", location);
    		plist.add(new IdentifierPattern(result));
		}

		if (state != null)	// Two args, called Sigma~ and Sigma
		{
			plist.add(new IdentifierPattern(state.name.getOldName()));
			plist.add(new IdentifierPattern(state.name));
		}
		else if (base.isVDMPP() && !accessSpecifier.isStatic)
		{
			// Two arguments called "self~" and "self"
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
		boolean patterns = false;

		for (Pattern p: parameterPatterns)
		{
			if (!(p instanceof IdentifierPattern) &&
				!(p instanceof IgnorePattern))
			{
				patterns = true;
				break;
			}
		}

		if (patterns)
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

		obligations.addAll(body.getProofObligations(ctxt));

		if (!TypeComparator.isSubType(actualResult, type.result))
		{
			obligations.add(new SubTypeObligation(this, actualResult, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "explicit operation";
	}

	@Override
	public boolean isFunctionOrOperation()
	{
		return true;
	}

	@Override
	public boolean isCallableOperation()
	{
		return true;
	}
}
