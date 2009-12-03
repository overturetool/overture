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

package org.overturetool.vdmj.expressions;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.pog.POForAllContext;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.Value;


public class LambdaExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final List<TypeBind> bindList;
	public final Expression expression;

	private FunctionType type;
	private PatternList paramPatterns;
	private DefinitionList paramDefinitions;

	public LambdaExpression(LexLocation location,
			List<TypeBind> bindList, Expression expression)
	{
		super(location);
		this.bindList = bindList;
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return "(lambda " + bindList + " & " + expression + ")";
	}

	@Override
	public Type typeCheck(Environment base, TypeList qualifiers, NameScope scope)
	{
		List<MultipleBind> mbinds = new Vector<MultipleBind>();
		TypeList ptypes = new TypeList();

		paramPatterns = new PatternList();
		paramDefinitions = new DefinitionList();

		for (TypeBind tb: bindList)
		{
			mbinds.addAll(tb.getMultipleBindList());
			paramDefinitions.addAll(tb.pattern.getDefinitions(tb.type, NameScope.LOCAL));
			paramPatterns.add(tb.pattern);
			ptypes.add(tb.type.typeResolve(base, null));
		}

		paramDefinitions.implicitDefinitions(base);
		paramDefinitions.typeCheck(base, scope);

		Definition def = new MultiBindListDefinition(location, mbinds);
		def.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(def, base, scope);
		Type result = expression.typeCheck(local, null, scope);
		local.unusedCheck();

		type = new FunctionType(location, true, ptypes, result);
		return type;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		// Free variables are everything currently visible from this
		// context (but without the context chain).

		Context free = ctxt.getVisibleVariables();

		return new FunctionValue(location, "lambda", type,
			paramPatterns, expression, free);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return expression.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (TypeBind tb: bindList)
		{
			obligations.addAll(tb.getProofObligations(ctxt));
		}

		ctxt.push(new POForAllContext(this));
		obligations.addAll(expression.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "lambda";
	}
}
