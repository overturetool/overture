/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

// This must be in the default package to work with VDMJ's native delegation.

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.CharacterValue;
import org.overturetool.vdmj.values.NilValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.VoidValue;

/**
 * This class contains the code for native IO operations.
 */

public class IO
{
	private static String lastError = "";

	public static Value writeval(Value tval)
	{
		String text = tval.toString();

		Console.out.print(text);
		Console.out.flush();

		return new BooleanValue(true);
	}

	public static Value fwriteval(Value fval, Value tval, Value dval)
	{
		String filename = stringOf(fval);
		String text = stringOf(tval);
		String fdir = dval.toString();	// <start>|<append>

		try
		{
			FileOutputStream fos =
				new FileOutputStream(filename, fdir.equals("<append>"));

			fos.write(text.getBytes(Console.charset));
			fos.close();
		}
		catch (IOException e)
		{
			lastError = e.getMessage();
			return new BooleanValue(false);
		}

		return new BooleanValue(true);
	}

	public static Value freadval(Value fval)
	{
		ValueList result = new ValueList();

		try
		{
			File file = new File(stringOf(fval).replace('/', File.separatorChar));

			if (file.getAbsolutePath().contains(File.pathSeparator))
			{
				file = new File(new File(".").getParentFile(), file.getAbsolutePath());
				System.out.println(file.getAbsolutePath());
			}

			LexTokenReader ltr = new LexTokenReader(file, Dialect.VDM_PP);
			ExpressionReader reader = new ExpressionReader(ltr);
			reader.setCurrentModule("IO");
			Expression exp = reader.readExpression();

			try
			{
				Interpreter ip = Interpreter.getInstance();
				ip.typeCheck(exp, ip.getGlobalEnvironment());
			}
			catch (Exception e)
			{
				// OK
			}

			result.add(new BooleanValue(true));
			Context ectxt = new Context(null, "freadval", null);
			ectxt.setThreadState(null, CPUClassDefinition.virtualCPU);
			result.add(exp.eval(ectxt));
		}
		catch (Exception e)
		{
			lastError = e.toString();
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}

	public static Value fecho(Value fval, Value tval, Value dval)
	{
		String text = stringOf(tval);
		String filename = fval.toString().replace("\"", "");

		if (filename.equals("[]"))
		{
			Console.out.print(text);
			Console.out.flush();
		}
		else
		{
			String fdir = dval.toString();	// <start>|<append>

			try
			{
				FileOutputStream fos =
					new FileOutputStream(filename, fdir.equals("<append>"));

				fos.write(text.getBytes(Console.charset));
				fos.close();
			}
			catch (IOException e)
			{
				lastError = e.getMessage();
				return new BooleanValue(false);
			}
		}

		return new BooleanValue(true);
	}

	public static Value ferror()
	{
		return new SeqValue(lastError);
	}

	// We need this because the toString of the Value converts special
	// characters back to their quoted form.

	private static String stringOf(Value val)
	{
		StringBuilder s = new StringBuilder();
		val = val.deref();

		if (val instanceof SeqValue)
		{
			SeqValue sv = (SeqValue)val;

			for (Value v: sv.values)
			{
				v = v.deref();

				if (v instanceof CharacterValue)
				{
					CharacterValue cv = (CharacterValue)v;
					s.append(cv.unicode);
				}
				else
				{
					s.append("?");
				}
			}

			return s.toString();
		}
		else
		{
			return val.toString();
		}
	}

	public static Value print(Value v)
	{
		Console.out.printf("%s", v);
		Console.out.flush();
		return new VoidValue();
	}

	public static Value printf(Value fv, Value vs)
		throws ValueException
	{
		String format = stringOf(fv);
		ValueList values = vs.seqValue(null);
		Console.out.printf(format, values.toArray());
		Console.out.flush();
		return new VoidValue();
	}
}
