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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.NotYetSpecifiedExpression;
import org.overturetool.vdmj.expressions.SubclassResponsibilityExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.pog.SatisfiabilityObligation;
import org.overturetool.vdmj.pog.ParameterPatternObligation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.POFunctionDefinitionContext;
import org.overturetool.vdmj.pog.POFunctionResultContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.FuncPostConditionObligation;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.PatternTypePair;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;


/**
 * A class to hold an implicit function definition.
 */

public class ImplicitFunctionDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final LexNameList typeParams;
	public final List<PatternListTypePair> parameterPatterns;
	public final PatternTypePair result;
	public final Expression body;
	public final Expression precondition;
	public final Expression postcondition;
	public final LexIdentifierToken measure;

	public FunctionType type;
	public ExplicitFunctionDefinition predef;
	public ExplicitFunctionDefinition postdef;

	public boolean recursive = false;
	public boolean isUndefined = false;
	public int measureLexical = 0;
	private Type actualResult;

	public ImplicitFunctionDefinition(LexNameToken name,
		NameScope scope, LexNameList typeParams,
		List<PatternListTypePair> parameterPatterns,
		PatternTypePair result,
		Expression body,
		Expression precondition,
		Expression postcondition, LexIdentifierToken measure)
	{
		super(Pass.DEFS, name.location, name, scope);

		this.typeParams = typeParams;
		this.parameterPatterns = parameterPatterns;
		this.result = result;
		this.body = body;
		this.precondition = precondition;
		this.postcondition = postcondition;
		this.measure = measure;

		TypeList ptypes = new TypeList();

		for (PatternListTypePair ptp: parameterPatterns)
		{
			ptypes.addAll(ptp.getTypeList());
		}

		// NB: implicit functions are always +> total, apparently
		type = new FunctionType(location, false, ptypes, result.type);
		type.definitions = new DefinitionList(this);
	}

	@Override
	public String toString()
	{
		return	accessSpecifier + " " +	name.name +
				(typeParams == null ? "" : "[" + typeParams + "]") +
				Utils.listToString("(", parameterPatterns, ", ", ")") + result +
				(body == null ? "" : " ==\n\t" + body) +
				(precondition == null ? "" : "\n\tpre " + precondition) +
				(postcondition == null ? "" : "\n\tpost " + postcondition);
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		if (precondition != null)
		{
			predef = getPreDefinition();
			predef.markUsed();
		}
		else
		{
			predef = null;
		}

		if (postcondition != null)
		{
			postdef = getPostDefinition();
			postdef.markUsed();
		}
		else
		{
			postdef = null;
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

			if (body instanceof SubclassResponsibilityExpression)
			{
				classDefinition.isAbstract = true;
			}
		}

		if (body instanceof SubclassResponsibilityExpression ||
			body instanceof NotYetSpecifiedExpression)
		{
			isUndefined = true;
		}

		if (precondition != null)
		{
			predef.typeResolve(base);
		}

		if (postcondition != null)
		{
			postdef.typeResolve(base);
		}

		for (PatternListTypePair pltp: parameterPatterns)
		{
			pltp.typeResolve(base);
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (typeParams != null)
		{
			type.typeParamCheck(typeParams);

			for (LexNameToken pname: typeParams)
			{
				Definition p = new LocalDefinition(
					pname.location, pname, scope, new ParameterType(pname));

				p.markUsed();
				defs.add(p);
			}
		}

		DefinitionSet argdefs = new DefinitionSet();

		for (PatternListTypePair pltp: parameterPatterns)
		{
			argdefs.addAll(pltp.getDefinitions(NameScope.LOCAL));
		}

		defs.addAll(argdefs.asList());
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

			actualResult = body.typeCheck(local, null, scope);

			if (!TypeComparator.compatible(result.type, actualResult))
			{
				report(3029, "Function returns unexpected type");
				detail2("Actual", actualResult, "Expected", result.type);
			}
		}

		if (type.narrowerThan(accessSpecifier))
		{
			report(3030, "Function type narrows function");
		}

		if (predef != null)
		{
			Type b = predef.body.typeCheck(local, null, NameScope.NAMES);
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
	    			new FlatCheckedEnvironment(postdefs, local, NameScope.NAMES);
	    		post.setStatic(accessSpecifier);
	    		post.setEnclosingDefinition(this);
				b = postdef.body.typeCheck(post, null, NameScope.NAMES);
				post.unusedCheck();
			}
			else
			{
				b = postdef.body.typeCheck(local, null, NameScope.NAMES);
			}

			BooleanType expected = new BooleanType(location);

			if (!b.isType(BooleanType.class))
			{
				report(3018, "Postcondition returns unexpected type");
				detail2("Actual", b, "Expected", expected);
			}
		}

		if (measure == null && recursive)
		{
			warning(5012, "Recursive function has no measure");
		}
		else if (measure != null)
		{
			LexNameToken mname = new LexNameToken(name.module, measure);
			if (base.isVDMPP()) mname.setTypeQualifier(type.parameters);
			Definition mdef = base.findName(mname, scope);

			if (body == null)
			{
				measure.report(3273, "Measure not allowed for an implicit function");
			}
			else if (mdef == null)
			{
				measure.report(3270, "Measure " + mname + " is not in scope");
			}
			else if (!(mdef instanceof ExplicitFunctionDefinition))
			{
				measure.report(3271, "Measure " + mname + " is not an explicit function");
			}
			else
			{
				FunctionType mtype = (FunctionType)mdef.getType();

				if (!(mtype.result instanceof NaturalType))
				{
					if (mtype.result.isProduct())
					{
						ProductType pt = mtype.result.getProduct();

						for (Type t: pt.types)
						{
							if (!(t instanceof NaturalType))
							{
								measure.report(3272,
									"Measure range is not a nat, or a nat tuple");
								measure.detail("Actual", mtype.result);
							}
						}

						measureLexical = pt.types.size();
					}
					else
					{
						measure.report(3272,
							"Measure range is not a nat, or a nat tuple");
						measure.detail("Actual", mtype.result);
					}
				}
			}
		}

		if (!(body instanceof NotYetSpecifiedExpression) &&
			!(body instanceof SubclassResponsibilityExpression))
		{
			local.unusedCheck();
		}
	}

	@Override
	public Type getType()
	{
		return type;		// NB overall "->" type, not result type
	}

	public FunctionType getType(TypeList actualTypes)
	{
		Iterator<Type> ti = actualTypes.iterator();
		FunctionType ftype = type;

		for (LexNameToken pname: typeParams)
		{
			Type ptype = ti.next();
			ftype = (FunctionType)ftype.polymorph(pname, ptype);
		}

		return ftype;
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
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();
		Context free = ctxt.getVisibleVariables();

		FunctionValue prefunc =
			(predef == null) ? null : new FunctionValue(predef, null, null, free);

		FunctionValue postfunc =
			(postdef == null) ? null : new FunctionValue(postdef, null, null, free);

		// Note, body may be null if it is really implicit. This is caught
		// when the function is invoked. The value is needed to implement
		// the pre_() expression for implicit functions.

		FunctionValue func = new FunctionValue(this, prefunc, postfunc, free);
		func.isStatic = accessSpecifier.isStatic;
		nvl.add(new NameValuePair(name, func));

		if (predef != null)
		{
			nvl.add(new NameValuePair(predef.name, prefunc));
		}

		if (postdef != null)
		{
			nvl.add(new NameValuePair(postdef.name, postfunc));
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			// This is needed for recursive local functions
			free.putList(nvl);
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

	public List<PatternList> getParamPatternList()
	{
		List<PatternList> parameters = new Vector<PatternList>();
		PatternList plist = new PatternList();

		for (PatternListTypePair pl: parameterPatterns)
		{
			plist.addAll(pl.patterns);
		}

		parameters.add(plist);
		return parameters;
	}

	private ExplicitFunctionDefinition getPreDefinition()
	{
		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getPreName(precondition.location), NameScope.GLOBAL,
			typeParams, type.getPreType(),
			getParamPatternList(), precondition, null, null, false, null);

		def.setAccessSpecifier(accessSpecifier);
		def.classDefinition = classDefinition;
		return def;
	}

	private ExplicitFunctionDefinition getPostDefinition()
	{
		List<PatternList> parameters = getParamPatternList();
		parameters.get(0).add(result.pattern);

		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getPostName(postcondition.location), NameScope.GLOBAL,
			typeParams, type.getPostType(),
			parameters, postcondition, null, null, false, null);

		def.setAccessSpecifier(accessSpecifier);
		def.classDefinition = classDefinition;
		return def;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		LexNameList pids = new LexNameList();

		for (PatternListTypePair pltp: parameterPatterns)
		{
			for (Pattern p: pltp.patterns)
			{
				pids.addAll(p.getVariableNames());
			}
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
			if (body != null)	// else satisfiability, below
			{
				ctxt.push(new POFunctionDefinitionContext(this, false));
				obligations.add(new FuncPostConditionObligation(this, ctxt));
				ctxt.pop();
			}

			ctxt.push(new POFunctionResultContext(this));
			obligations.addAll(postcondition.getProofObligations(ctxt));
			ctxt.pop();
		}

		ctxt.push(new POFunctionDefinitionContext(this, false));

		if (body == null)
		{
			if (postcondition != null)
			{
				obligations.add(
					new SatisfiabilityObligation(this, ctxt));
			}
		}
		else
		{
    		obligations.addAll(body.getProofObligations(ctxt));

			if (isUndefined ||
				!TypeComparator.isSubType(actualResult, type.result))
			{
				obligations.add(new SubTypeObligation(
					this, type.result, actualResult, ctxt));
			}
		}

		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "implicit function";
	}

	@Override
	public boolean isFunctionOrOperation()
	{
		return true;
	}
}
