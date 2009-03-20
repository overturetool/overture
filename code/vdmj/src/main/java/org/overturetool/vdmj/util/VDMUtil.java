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

package org.overturetool.vdmj.util;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.NaturalOneValue;
import org.overturetool.vdmj.values.NilValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;

public class VDMUtil
{
	public static Value set2seq(Context ctxt)
	{
		try
		{
			Value arg = ctxt.lookup(new LexNameToken("VDMUtil", "x", null));
			ValueSet set = arg.setValue(ctxt);
			ValueList list = new ValueList();
			list.addAll(set);
			return new SeqValue(list);
		}
		catch (ValueException e)
		{
			throw new ContextException(e, ctxt.location);
		}
	}

	public static Value get_file_pos(Context ctxt)
	{
		try
		{
			ValueList tuple = new ValueList();
			Context outer = ctxt.getRoot().outer;
			RootContext root = outer.getRoot();

			tuple.add(new SeqValue(ctxt.location.file.getPath()));
			tuple.add(new NaturalOneValue(ctxt.location.startLine));
			tuple.add(new NaturalOneValue(ctxt.location.startPos));
			tuple.add(new SeqValue(ctxt.location.module));

			int bra = root.title.indexOf('(');

			if (bra > 0)
			{
    			tuple.add(new SeqValue(root.title.substring(0, bra)));
			}
			else
			{
				tuple.add(new SeqValue(""));
			}

			return new TupleValue(tuple);
		}
		catch (ValueException e)
		{
			throw new ContextException(e, ctxt.location);
		}
		catch (Exception e)
		{
			throw new ContextException(4076, e.getMessage(), ctxt.location, ctxt);
		}
	}

	public static Value val2seq_of_char(Context ctxt)
	{
		Value arg = ctxt.lookup(new LexNameToken("VDMUtil", "x", null));
		return new SeqValue(arg.toString());
	}

	public static Value seq_of_char2val(Context ctxt)
	{
		ValueList result = new ValueList();

		try
		{
			Value fval = ctxt.lookup(new LexNameToken("VDMUtil", "s", null));
			String expression = fval.toString().replace("\"", "");
			LexTokenReader ltr = new LexTokenReader(expression, Dialect.VDM_PP);
			ExpressionReader reader = new ExpressionReader(ltr);
			reader.setCurrentModule("VDMUtil");
			Expression exp = reader.readExpression();
			result.add(new BooleanValue(true));
			result.add(exp.eval(ctxt));
		}
		catch (Exception e)
		{
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}
}
