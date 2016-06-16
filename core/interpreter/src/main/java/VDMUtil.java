import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;

/*******************************************************************************
 * Copyright (C) 2008, 2009 Fujitsu Services Ltd. Author: Nick Battle This file is part of VDMJ. VDMJ is free software:
 * you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. VDMJ is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with VDMJ. If not, see <a href="http://www.gnu.org/licenses/">gnu license</a>.
 ******************************************************************************/

// This must be in the default package to work with VDMJ's native delegation.

public class VDMUtil
{
	public static Value set2seq(Value arg) throws ValueException
	{
		ValueSet set = arg.setValue(null);
		ValueList list = new ValueList();
		list.addAll(set);
		return new SeqValue(list);
	}

	public static Value val2seq_of_char(Value arg)
	{
		return new SeqValue(arg.toString());
	}

	public static Value seq_of_char2val(Value arg)
	{
		ValueList result = new ValueList();

		try
		{
			SeqValue seq = (SeqValue) arg.deref();
			StringBuilder expression = new StringBuilder();

			for (Value v : seq.values)
			{
				CharacterValue ch = (CharacterValue) v.deref();
				expression.append(ch.unicode);
			}

			LexTokenReader ltr = new LexTokenReader(expression.toString(), Dialect.VDM_PP);
			ExpressionReader reader = new ExpressionReader(ltr);
			reader.setCurrentModule("VDMUtil");
			PExp exp = reader.readExpression();
			result.add(new BooleanValue(true));
			Context ctxt = new Context(Interpreter.getInstance().getAssistantFactory(), null, "seq_of_char2val", null);
			ctxt.setThreadState(null, null);
			result.add(exp.apply(VdmRuntime.getExpressionEvaluator(), ctxt));
		} catch (Exception e)
		{
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}

	public static Value classname(Value arg)
	{
		Value a = arg.deref();

		if (a instanceof ObjectValue)
		{
			ObjectValue obj = (ObjectValue) a;
			return new SeqValue(obj.type.getName().getName());
		} else
		{
			return new NilValue();
		}
	}
}
