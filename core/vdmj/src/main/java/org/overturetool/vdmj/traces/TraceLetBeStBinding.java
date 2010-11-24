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

package org.overturetool.vdmj.traces;

import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.SkipStatement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Quantifier;
import org.overturetool.vdmj.values.QuantifierList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 * A class representing a let-be-st trace binding.
 */

public class TraceLetBeStBinding extends TraceDefinition
{
    private static final long serialVersionUID = 1L;
	public final MultipleBind bind;
	public final Expression stexp;
	public final TraceDefinition body;

	private MultiBindListDefinition def = null;

	public TraceLetBeStBinding(
		LexLocation location, MultipleBind bind, Expression stexp, TraceDefinition body)
	{
		super(location);
		this.bind = bind;
		this.stexp = stexp;
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "let " + bind +
			(stexp == null ? "" : " be st " + stexp.toString()) + " in " + body;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		def = new MultiBindListDefinition(bind.location, bind.getMultipleBindList());
		def.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(def, base, scope);

		if (stexp != null &&
			!stexp.typeCheck(local, null, scope).isType(BooleanType.class))
		{
			TypeChecker.report(3225,
				"Such that clause is not boolean", stexp.location);
		}

		body.typeCheck(local, scope);
		local.unusedCheck();
	}

	@Override
	public TraceNode expand(Context ctxt)
	{
		AlternativeTraceNode node = new AlternativeTraceNode();

		QuantifierList quantifiers = new QuantifierList();

		for (MultipleBind mb: def.bindings)
		{
			ValueList bvals = mb.getBindValues(ctxt);

			for (Pattern p: mb.plist)
			{
				Quantifier q = new Quantifier(p, bvals);
				quantifiers.add(q);
			}
		}

		quantifiers.init();

		if (quantifiers.finished())		// No entries at all
		{
			node.alternatives.add(new StatementTraceNode(new SkipStatement(location)));
			return node;
		}

		try
		{
			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(location, "TRACE", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(stexp == null || stexp.eval(evalContext).boolValue(ctxt)))
				{
					TraceNode exp = body.expand(evalContext);
					exp.setVariables(new TraceVariableList(evalContext));
					node.alternatives.add(exp);
				}
			}
		}
        catch (ValueException e)
        {
        	throw new ContextException(e, location);
        }

		return node;
	}
}
