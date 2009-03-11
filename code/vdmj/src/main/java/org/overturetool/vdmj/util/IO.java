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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.NilValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 * This class contains the code for native IO operations.
 */

public class IO
{
	private static String lastError = "";

	public static BooleanValue writeval(Context ctxt)
	{
		Value tval = ctxt.lookup(new LexNameToken("IO", "val", null));
		String text = tval.toString();

		Console.out.print(text);
		Console.out.flush();

		return new BooleanValue(true);
	}

	public static BooleanValue fwriteval(Context ctxt)
	{
		Value tval = ctxt.lookup(new LexNameToken("IO", "val", null));
		String text = tval.toString();

		Value fval = ctxt.lookup(new LexNameToken("IO", "filename", null));
		String filename = fval.toString().replace("\"", "");

		Value dval = ctxt.lookup(new LexNameToken("IO", "fdir", null));
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

	public static TupleValue freadval(Context ctxt)
	{
		ValueList result = new ValueList();

		try
		{
			Value fval = ctxt.lookup(new LexNameToken("IO", "f", null));
			String filename = fval.toString().replace("\"", "");
			File file = new File(filename);
			LexTokenReader ltr = new LexTokenReader(file, Dialect.VDM_PP);
			ExpressionReader reader = new ExpressionReader(ltr);
			reader.setCurrentModule("IO");
			Expression exp = reader.readExpression();

			String caller = ctxt.location.module;
			Interpreter ip = Interpreter.getInstance();
			ClassDefinition classdef = ip.findClass(caller);

			if (classdef == null)
			{
				throw new Exception("Class " + caller + " not found");
			}

			Environment env = new FlatEnvironment(
				classdef.getSelfDefinition(),
				new PrivateClassEnvironment(classdef, ip.getGlobalEnvironment()));

			ip.typeCheck(exp, env);

			result.add(new BooleanValue(true));
			result.add(exp.eval(new Context(ctxt.location, "freadval", null)));
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

	public static BooleanValue fecho(Context ctxt)
	{
		Value tval = ctxt.lookup(new LexNameToken("IO", "text", null));
		String text = tval.toString();
		text = text.substring(1, text.length() - 1);

		Value fval = ctxt.lookup(new LexNameToken("IO", "filename", null));
		String filename = fval.toString().replace("\"", "");

		if (filename.equals("[]"))
		{
			Console.out.print(text);
			Console.out.flush();
		}
		else
		{
			Value dval = ctxt.lookup(new LexNameToken("IO", "fdir", null));
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
}
