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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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
import org.overturetool.vdmj.statements.TraceStatement;
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
	private String vppName = null;
	private String assertName = null;

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
		setNames("/Overture/syntax/", rpath);
		List<VDMMessage> actual = new Vector<VDMMessage>();
		parse(actual);
		checkErrors(actual);
	}

	protected void typecheck(String rpath) throws Exception
	{
		setNames("/Overture/typecheck/", rpath);
		List<VDMMessage> actual = new Vector<VDMMessage>();
		ClassList classes = parse(actual);

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
		actual.addAll(TypeChecker.getWarnings());
		checkErrors(actual);
	}

	protected void runtime(String rpath) throws Exception
	{
		setNames("/Overture/runtime/", rpath);
		List<VDMMessage> actual = new Vector<VDMMessage>();
		ClassList classes = parse(actual);

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
		actual.addAll(TypeChecker.getWarnings());

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no typecheck errors", 0, actual.size());
		}

		try
		{
			Interpreter interpreter = new ClassInterpreter(classes);
			interpreter.init(null);

			interpreter.execute(new File(assertName));
			fail("Expecting a runtime error");
		}
		catch (ContextException e)
		{
			Console.out.println(e);
			actual.add(new VDMError(e));
			checkErrors(actual);
		}
		catch (Exception e)
		{
			Console.out.print("Caught: " + e + " in " + assertName);
			throw e;
		}
	}

	protected static enum ResultType
	{
		TRUE, VOID, UNDEFINED, ERROR
	}

	protected void evaluate(String rpath, ResultType rt) throws Exception
	{
		evaluate(rpath, rt, 0);
	}

	protected void evaluate(String rpath, ResultType rt, int error) throws Exception
	{
		setNames("/Overture/evaluate/", rpath);
		List<VDMMessage> actual = new Vector<VDMMessage>();
		ClassList classes = parse(actual);

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
		actual.addAll(TypeChecker.getWarnings());

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no typecheck errors", 0, actual.size());
		}

		try
		{
			Interpreter interpreter = new ClassInterpreter(classes);
			interpreter.init(null);

			Value result = interpreter.execute(new File(assertName));

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
			assertTrue("Expecting runtime error " + error, error == 0);
		}
		catch (ContextException e)
		{
			Console.out.println(e);

			if (e.number != error)
			{
				fail("Unexpected runtime error: " + e);
			}
		}
		catch (Exception e)
		{
			fail("Caught: " + e + " in " + assertName);
		}
	}

	protected void combtest(String rpath, String testExp) throws Exception
	{
		combtest(rpath, rpath, testExp, 0);	// No expected error
	}

	protected void combtest(String rpath, String testExp, int error) throws Exception
	{
		combtest(rpath, rpath, testExp, error);
	}

	protected void combtest(String vpath, String apath, String testExp, int error)
		throws Exception
	{
		Console.out.println("Processing " + apath + "...");

		setVppName("/Overture/combtest/", vpath);
		setAssertName("/Overture/combtest/", apath);

		List<VDMMessage> actual = new Vector<VDMMessage>();
		ClassList classes = parse(actual);

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
		// actual.addAll(TypeChecker.getWarnings());

		if (!actual.isEmpty())
		{
			Console.out.println(Utils.listToString(actual, "\n"));
			assertEquals("Expecting no typecheck errors", 0, actual.size());
		}

		try
		{
			Interpreter interpreter = new ClassInterpreter(classes);
			interpreter.init(null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(out);
			TraceStatement.setOutput(pw);

			interpreter.execute(testExp, null);

			VDMThreadSet.abortAll();
			pw.close();
			String result = out.toString();
			String expected = readFile(new File(assertName));

			if (!result.equals(expected))
			{
				Console.out.println(assertName + " should be:\n" + result);
			}

			assertEquals("Evaluation error", expected, result);
			assertTrue("Expecting runtime error " + error, error == 0);
		}
		catch (ContextException e)
		{
			Console.out.println(e);

			if (e.number != error)
			{
				fail("Unexpected runtime error: " + e);
			}
		}
		catch (Exception e)
		{
			fail("Caught: " + e + " in " + assertName);
		}
	}

	private void checkErrors(List<VDMMessage> actual) throws Exception
	{
		try
		{
			Interpreter interpreter = new ClassInterpreter(new ClassList());
			interpreter.init(null);

			Value assertions = interpreter.execute(new File(assertName));

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
			fail("Caught: " + e + " in " + assertName);
		}
	}

	private String readFile(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);

		String line = br.readLine();

		while (line != null)
		{
			pw.println(line);
			line = br.readLine();
		}

		br.close();
		pw.close();
		return out.toString();	// Same EOL convention as local machine
	}

	private void setNames(String prefix, String root)
	{
		setVppName(prefix, root);
		setAssertName(prefix, root);
		Console.out.println("Processing " + prefix + root + "...");
	}

	private void setVppName(String prefix, String root)
	{
		URL rurl = getClass().getResource(prefix + root + ".vpp");

		if (rurl == null)
		{
			fail("Cannot find resource: " + prefix + root + ".vpp");
		}

		vppName = rurl.getPath();
	}

	private void setAssertName(String prefix, String root)
	{
		URL rurl = getClass().getResource(prefix + root + ".assert");

		if (rurl == null)
		{
			fail("Cannot find resource: " + prefix + root + ".assert");
		}

		assertName = rurl.getPath();
	}

	private ClassList parse(List<VDMMessage> messages)
		throws Exception
	{
		ClassList classes = null;
		String parser = System.getProperty("parser");

		if (parser == null || parser.equalsIgnoreCase("vdmj"))
		{
    		LexTokenReader ltr = new LexTokenReader(new File(vppName), Dialect.VDM_RT);
    		ClassReader cr = new ClassReader(ltr);
    		classes = cr.readClasses();
    		cr.close();
    		messages.addAll(cr.getErrors());
    		messages.addAll(cr.getWarnings());
		}
		else if (parser.equalsIgnoreCase("overture"))
		{
			OvertureReader or = new OvertureReader(new File(vppName));
			classes = or.readClasses();
			or.close();
    		messages.addAll(or.getErrors());
    		messages.addAll(or.getWarnings());
		}
		else
		{
			fail("-D parser property must be 'overture' or 'vdmj', not " + parser);
		}

		return classes;
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
