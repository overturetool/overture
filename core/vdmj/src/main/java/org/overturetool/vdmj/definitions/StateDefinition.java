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

import org.overturetool.vdmj.expressions.EqualsExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.StateInitExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.State;


/**
 * A class to hold a module's state definition.
 */

public class StateDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final List<Field> fields;
	public final Pattern invPattern;
	public final Expression invExpression;
	public final Pattern initPattern;
	public final Expression initExpression;

	public LocalDefinition recordDefinition = null;
	public ExplicitFunctionDefinition invdef = null;
	public FunctionValue invfunc = null;
	public ExplicitFunctionDefinition initdef = null;
	public FunctionValue initfunc = null;

	private final DefinitionList statedefs;
	private Type recordType;
	private State moduleState = null;

	public StateDefinition(LexNameToken name, List<Field> fields,
		Pattern invPattern, Expression invExpression,
		Pattern initPattern, Expression initExpression)
	{
		super(Pass.TYPES, name.location, name, NameScope.STATE);

		this.fields = fields;
		this.invPattern = invPattern;
		this.invExpression = invExpression;
		this.initPattern = initPattern;
		this.initExpression = initExpression;

		statedefs = new DefinitionList();

		for (Field f: fields)
		{
			statedefs.add(new LocalDefinition(
				f.tagname.location, f.tagname, NameScope.STATE, f.type));

			LocalDefinition ld = new LocalDefinition(f.tagname.location,
				f.tagname.getOldName(), NameScope.OLDSTATE, f.type);

			ld.markUsed();		// Else we moan about unused ~x names
			statedefs.add(ld);
		}

		recordType = new RecordType(name, fields);
		recordDefinition = new LocalDefinition(location, name, NameScope.STATE, recordType);
		recordDefinition.markUsed();	// Can't be exported anyway
		statedefs.add(recordDefinition);
	}

	@Override
	public String toString()
	{
		return "state " + name + "of\n" + fields +
			(invPattern == null ? "" : "\n\tinv " + invPattern + " == " + invExpression) +
    		(initPattern == null ? "" : "\n\tinit " + initPattern + " == " + initExpression);
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		if (invPattern != null)
		{
			invdef = getInvDefinition();
		}

		if (initPattern != null)
		{
			initdef = getInitDefinition();
		}
	}

	@Override
	public void typeResolve(Environment env)
	{
		for (Field f: fields)
		{
			try
			{
				f.typeResolve(env, null);
			}
			catch (TypeCheckException e)
			{
				f.unResolve();
				throw e;
			}
		}

		recordType = recordType.typeResolve(env, null);

		if (invPattern != null)
		{
			invdef.typeResolve(env);
		}

		if (initPattern != null)
		{
			initdef.typeResolve(env);
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		if (base.findStateDefinition() != this)
		{
			report(3047, "Only one state definition allowed per module");
			return;
		}

		statedefs.typeCheck(base, scope);

		if (invdef != null)
		{
			invdef.typeCheck(base, scope);
		}

		if (initdef != null)
		{
			if (!checkInit(base, scope))
			{
				warning(5010, "State init expression cannot be executed");
				detail("Expected", "p == p = mk_Record(...)");
			}
			else
			{
				initdef.typeCheck(base, scope);
			}
		}
	}

	private boolean checkInit(Environment base, NameScope scope)
	{
		if (initPattern instanceof IdentifierPattern &&
			initExpression instanceof EqualsExpression)
		{
			EqualsExpression ee = (EqualsExpression)initExpression;

			if (ee.left instanceof VariableExpression)
			{
				Type rhs = ee.right.typeCheck(base, null, scope);

				if (rhs.isRecord())
				{
					RecordType rt = rhs.getRecord();
					return rt.name.equals(name);
				}
			}
		}

		return false;
	}

	@Override
	public Definition findType(LexNameToken sought)
	{
		if (super.findName(sought, NameScope.STATE) != null)
		{
			return this;
		}

		return null;
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		if (scope.matches(NameScope.NAMES))
		{
    		if (invdef != null && invdef.findName(sought, scope) != null)
    		{
    			return invdef;
    		}

    		if (initdef != null && initdef.findName(sought, scope) != null)
    		{
    			return initdef;
    		}
		}

		if (scope.matches(NameScope.STATE))
		{
			if (recordDefinition.findName(sought, scope) != null)
			{
				return recordDefinition;
			}

    		for (Definition d: statedefs)
    		{
    			Definition def = d.findName(sought, scope);

    			if (def != null)
    			{
   					return def;
    			}
    		}
		}

		return null;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		if (invExpression != null)
		{
			Expression found = invExpression.findExpression(lineno);
			if (found != null) return found;
		}

		if (initExpression != null)
		{
			if (initExpression instanceof EqualsExpression)
			{
				EqualsExpression ee = (EqualsExpression)initExpression;
				Expression found = ee.right.findExpression(lineno);
				if (found != null) return found;
			}
		}

		return null;
	}

	@Override
	public Type getType()
	{
		return recordType;
	}

	@Override
	public void unusedCheck()
	{
		statedefs.unusedCheck();
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return statedefs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		return statedefs.getVariableNames();
	}

	public void initState(StateContext initialContext)
	{
		if (invdef != null)
		{
			invfunc = new FunctionValue(invdef, null, null, initialContext);
			initialContext.put(name.getInvName(location), invfunc);
		}

		if (initdef != null)
		{
			initfunc = new FunctionValue(initdef, null, null, initialContext);
			initialContext.put(name.getInitName(location), initfunc);
		}

		moduleState = new State(this);
		moduleState.initialize(initialContext);
	}

	public Context getStateContext()
	{
		return moduleState.getContext();
	}

	public State getState()
	{
		return moduleState;
	}

	private ExplicitFunctionDefinition getInvDefinition()
	{
		LexLocation loc = invPattern.location;
		PatternList params = new PatternList();
		params.add(invPattern);

		List<PatternList> parameters = new Vector<PatternList>();
		parameters.add(params);

		TypeList ptypes = new TypeList();
		ptypes.add(new UnresolvedType(name));
		FunctionType ftype =
			new FunctionType(loc, false, ptypes, new BooleanType(loc));

		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getInvName(loc), NameScope.GLOBAL,
			null, ftype, parameters, invExpression, null, null, true, false, null);

		ftype.definitions = new DefinitionList(def);
		return def;
	}

	private ExplicitFunctionDefinition getInitDefinition()
	{
		LexLocation loc = initPattern.location;
		PatternList params = new PatternList();
		params.add(initPattern);

		List<PatternList> parameters = new Vector<PatternList>();
		parameters.add(params);

		TypeList ptypes = new TypeList();
		ptypes.add(new UnresolvedType(name));
		FunctionType ftype =
			new FunctionType(loc, false, ptypes, new BooleanType(loc));

		Expression body = new StateInitExpression(this);

		ExplicitFunctionDefinition def =
			new ExplicitFunctionDefinition(name.getInitName(loc), NameScope.GLOBAL,
			null, ftype, parameters, body, null, null, false, false, null);

		ftype.definitions = new DefinitionList(def);
		return def;
	}

	@Override
	public String kind()
	{
		return "state";
	}
}
