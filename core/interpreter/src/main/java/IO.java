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
import java.io.Serializable;

import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.interpreter.VDMJ;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.VoidValue;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;

/**
 * This class contains the code for native IO operations.
 */

public class IO implements Serializable
{
	private static final long serialVersionUID = 1L;
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
		File file = getFile(fval);
		String text = tval.toString();// stringOf(tval);
		String fdir = dval.toString(); // <start>|<append>

		try
		{
			FileOutputStream fos = new FileOutputStream(file, fdir.equals("<append>"));

			fos.write(text.getBytes(Console.charset));
			fos.close();
		} catch (IOException e)
		{
			lastError = e.getMessage();
			return new BooleanValue(false);
		}

		return new BooleanValue(true);
	}

	// Note that this method is not callable via the native interface, since it
	// need access to the Context to call any type invariants involved while
	// reading the data.

	public static Value freadval(Value fval, Context ctxt)
	{
		ValueList result = new ValueList();

		try
		{
			File file = getFile(fval);

			LexTokenReader ltr = new LexTokenReader(file, Dialect.VDM_PP, VDMJ.filecharset);
			ExpressionReader reader = new ExpressionReader(ltr);
			reader.setCurrentModule("IO");
			PExp exp = reader.readExpression();

			Interpreter ip = Interpreter.getInstance();
			ip.typeCheck(exp, ip.getGlobalEnvironment());
			result.add(new BooleanValue(true));
			result.add(exp.apply(VdmRuntime.getExpressionEvaluator(), ctxt));
		} catch (Exception e)
		{
			lastError = e.toString();
			result = new ValueList();
			result.add(new BooleanValue(false));
			result.add(new NilValue());
		}

		return new TupleValue(result);
	}

	/**
	 * Gets the absolute path the file based on the filename parsed and the working dir of the IDE or the execution dir
	 * of VDMJ
	 * 
	 * @param fval
	 *            file name
	 * @return
	 */
	protected static File getFile(Value fval)
	{
		String path = stringOf(fval).replace('/', File.separatorChar);
		File file = new File(path);

		if (!file.isAbsolute())
		{
			file = new File(Settings.baseDir, path);
		}
		return file;
	}

	public static Value fecho(Value fval, Value tval, Value dval)
	{
		String text = stringOf(tval);
		String filename = fval.toString().replace("\"", "");

		if (filename.equals("[]"))
		{
			Console.out.print(text);
			Console.out.flush();
		} else
		{
			String fdir = dval.toString(); // <start>|<append>

			try
			{
				File file = getFile(fval);
				FileOutputStream fos = new FileOutputStream(file, fdir.equals("<append>"));

				fos.write(text.getBytes(Console.charset));
				fos.close();
			} catch (IOException e)
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

	protected static String stringOf(Value val)
	{
		StringBuilder s = new StringBuilder();
		val = val.deref();

		if (val instanceof SeqValue)
		{
			SeqValue sv = (SeqValue) val;

			for (Value v : sv.values)
			{
				v = v.deref();

				if (v instanceof CharacterValue)
				{
					CharacterValue cv = (CharacterValue) v;
					s.append(cv.unicode);
				} else
				{
					s.append("?");
				}
			}

			return s.toString();
		} else
		{
			return val.toString();
		}
	}

	public static Value print(Value v)
	{
		Console.out.printf("%s", stringOf(v));
		Console.out.flush();
		return new VoidValue();
	}

	public static Value println(Value v)
	{
		Console.out.printf("%s", stringOf(v));
		Console.out.printf("%s", "\n");
		Console.out.flush();
		return new VoidValue();
	}

	public static Value printf(Value fv, Value vs) throws ValueException
	{
		String format = stringOf(fv);
		ValueList values = vs.seqValue(null);
		Console.out.printf(format, values.toArray());
		Console.out.flush();
		return new VoidValue();
	}
	
	public static Value sprintf(Value fv, Value vs) throws ValueException
	{
		String format = stringOf(fv);
		ValueList values = vs.seqValue(null);
		return new SeqValue(String.format(format, values.toArray()));
	}

	public static Value toString(Value v)
	{
		String text = v.toString().replaceAll("\"", "");
		return new SeqValue(text);
	}
}
