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

package junit.overture;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMMessage;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.VDMThreadSet;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.OvertureReader;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.UndefinedValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;


import junit.framework.TestCase;

abstract public class OvertureTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	protected void syntax(String rpath) throws Exception
	{
		URL rurl = getClass().getResource("/Overture/syntax/" + rpath + ".vpp");

		if (rurl == null)
		{
			fail("Cannot find resource: " + rpath + ".vpp");
		}

		String vpppath = rurl.getPath();
		String path = vpppath.substring(0, vpppath.lastIndexOf('.'));

		Console.out.println("Processing " + path + "...");

		List<VDMMessage> actual = new Vector<VDMMessage>();
		String parser = System.getProperty("parser");

		if (parser == null || parser.equalsIgnoreCase("vdmj"))
		{
    		LexTokenReader ltr = new LexTokenReader(new File(vpppath), Dialect.VDM_PP);
    		ClassReader cr = new ClassReader(ltr);
    		cr.readClasses();
    		cr.close();
    		actual.addAll(cr.getErrors());
		}
		else if (parser.equalsIgnoreCase("overture"))
		{
			OvertureReader or = new OvertureReader(new File(vpppath));
			or.readClasses();
			or.close();
    		actual.addAll(or.getErrors());
		}
		else
		{
			fail("-D parser property must be 'overture' or 'vdmj', not " + parser);
		}

		checkErrors(actual, path + ".assert");
	}

	protected void typecheck(String rpath) throws Exception
	{
		URL rurl = getClass().getResource("/Overture/typecheck/" + rpath + ".vpp");

		if (rurl == null)
		{
			fail("Cannot find resource: " + rpath + ".vpp");
		}

		String vpppath = rurl.getPath();
		String path = vpppath.substring(0, vpppath.lastIndexOf('.'));

		Console.out.println("Processing " + path + "...");

		List<VDMMessage> actual = new Vector<VDMMessage>();
		String parser = System.getProperty("parser");
		ClassList classes = null;

		if (parser == null || parser.equalsIgnoreCase("vdmj"))
		{
    		LexTokenReader ltr = new LexTokenReader(new File(vpppath), Dialect.VDM_PP);
    		ClassReader cr = new ClassReader(ltr);
    		classes = cr.readClasses();
    		cr.close();
    		actual.addAll(cr.getErrors());
		}
		else if (parser.equalsIgnoreCase("overture"))
		{
			OvertureReader or = new OvertureReader(new File(vpppath));
			classes = or.readClasses();
			or.close();
    		actual.addAll(or.getErrors());
		}
		else
		{
			fail("-D parser property must be 'overture' or 'vdmj', not " + parser);
		}

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no syntax errors", 0, actual.size());
		}

		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		TypeChecker.printErrors(Console.out);
		TypeChecker.printWarnings(Console.out);

		actual.addAll(TypeChecker.getErrors());
		checkErrors(actual, path + ".assert");
	}

	protected void runtime(String rpath) throws Exception
	{
		URL rurl = getClass().getResource("/Overture/runtime/" + rpath + ".vpp");

		if (rurl == null)
		{
			fail("Cannot find resource: " + rpath + ".vpp");
		}

		String vpppath = rurl.getPath();
		String path = vpppath.substring(0, vpppath.lastIndexOf('.'));

		Console.out.println("Processing " + path + "...");

		List<VDMMessage> actual = new Vector<VDMMessage>();
		String parser = System.getProperty("parser");
		ClassList classes = null;

		if (parser == null || parser.equalsIgnoreCase("vdmj"))
		{
    		LexTokenReader ltr = new LexTokenReader(new File(vpppath), Dialect.VDM_PP);
    		ClassReader cr = new ClassReader(ltr);
    		classes = cr.readClasses();
    		cr.close();
    		actual.addAll(cr.getErrors());
		}
		else if (parser.equalsIgnoreCase("overture"))
		{
			OvertureReader or = new OvertureReader(new File(vpppath));
			classes = or.readClasses();
			or.close();
    		actual.addAll(or.getErrors());
		}
		else
		{
			fail("-D parser property must be 'overture' or 'vdmj', not " + parser);
		}

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no syntax errors", 0, actual.size());
		}

		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		TypeChecker.printErrors(Console.out);
		TypeChecker.printWarnings(Console.out);

		actual.addAll(TypeChecker.getErrors());

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no typecheck errors", 0, actual.size());
		}

		try
		{
			Interpreter interpreter = new ClassInterpreter(classes);
			interpreter.init(null);

			interpreter.execute(new File(path + ".assert"));
			fail("Expecting a runtime error");
		}
		catch (ContextException e)
		{
			actual.add(new VDMError(e));
			checkErrors(actual, path + ".assert");
		}
		catch (Exception e)
		{
			Console.out.print("Caught: " + e + " in " + path + ".assert");
			throw e;
		}
	}

	protected static enum ResultType
	{
		TRUE, VOID, UNDEFINED
	}

	protected void evaluate(String rpath, ResultType rt) throws Exception
	{
		URL rurl = getClass().getResource("/Overture/evaluate/" + rpath + ".vpp");

		if (rurl == null)
		{
			fail("Cannot find resource: " + rpath + ".vpp");
		}

		String vpppath = rurl.getPath();
		String path = vpppath.substring(0, vpppath.lastIndexOf('.'));

		Console.out.println("Processing " + path + "...");

		List<VDMMessage> actual = new Vector<VDMMessage>();
		String parser = System.getProperty("parser");
		ClassList classes = null;

		if (parser == null || parser.equalsIgnoreCase("vdmj"))
		{
    		LexTokenReader ltr = new LexTokenReader(new File(vpppath), Dialect.VDM_PP);
    		ClassReader cr = new ClassReader(ltr);
    		classes = cr.readClasses();
    		cr.close();
    		actual.addAll(cr.getErrors());
		}
		else if (parser.equalsIgnoreCase("overture"))
		{
			OvertureReader or = new OvertureReader(new File(vpppath));
			classes = or.readClasses();
			or.close();
    		actual.addAll(or.getErrors());
		}
		else
		{
			fail("-D parser property must be 'overture' or 'vdmj', not " + parser);
		}

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no syntax errors", 0, actual.size());
		}

		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		TypeChecker.printErrors(Console.out);
		TypeChecker.printWarnings(Console.out);

		actual.addAll(TypeChecker.getErrors());

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no typecheck errors", 0, actual.size());
		}

		try
		{
			Interpreter interpreter = new ClassInterpreter(classes);
			interpreter.init(null);

			Value result = interpreter.execute(new File(path + ".assert"));

			VDMThreadSet.abortAll();
			Console.out.println("Result = " + result);
			Value expected = null;

			switch (rt)
			{
				case TRUE:
					expected = new BooleanValue(true);
					break;

				case VOID:
					expected = new VoidValue();
					break;

				case UNDEFINED:
					expected = new UndefinedValue();
					break;
				}

			assertEquals("Evaluation error", expected, result);
		}
		catch (ContextException e)
		{
			fail("Unexpected runtime error: " + e);
		}
		catch (Exception e)
		{
			fail("Caught: " + e + " in " + path + ".assert");
		}
	}

	private void checkErrors(List<VDMMessage> actual, String assertFile)
		throws Exception
	{
		try
		{
			Interpreter interpreter = new ClassInterpreter(new ClassList());
			interpreter.init(null);

			Value assertions = interpreter.execute(new File(assertFile));

			assertTrue("Expecting error list", assertions instanceof SeqValue);

			List<VDMMessage> expected = new Vector<VDMMessage>();

			for (Value ex: assertions.seqValue(null))
			{
				int n = (int)ex.intValue(null);
				expected.add(new VDMMessage(n));
			}

			if (!actual.equals(expected))
			{
				Console.out.println("Expected errors: " + listErrs(expected));
				Console.out.println("Actual errors: " + listErrs(actual));
				Console.out.println(Utils.listToString(actual, "\n"));
				fail("Actual errors not as expected");
			}
		}
		catch (Exception e)
		{
			fail("Caught: " + e + " in " + assertFile);
		}
	}

	private String listErrs(List<VDMMessage> list)
	{
		StringBuilder sb = new StringBuilder("[");
		String sep = "";

		for (VDMMessage m: list)
		{
			sb.append(sep);
			sb.append(m.number);
			sep = ", ";
		}

		sb.append("]");
		return sb.toString();
	}
}
