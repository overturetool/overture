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

package junit;

import java.io.File;
import java.net.URL;
import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class VDMBookTest extends VDMTestCase
{
	private void process(String resource, String aresource) throws Exception
	{
		Console.out.println("Processing " + resource + "...");

		URL rurl = getClass().getResource("/VDMBook/" + resource);
		String file = rurl.getPath();
		URL aurl = getClass().getResource("/VDMBook/" + aresource);
		String assertions = aurl.getPath();

		long before = System.currentTimeMillis();
		ClassList classes = parseClasses(file);
		long after = System.currentTimeMillis();

		Console.out.println("Parsed " + classes.size() + " classes in " +
   			(double)(after-before)/1000 + " secs. ");

		before = System.currentTimeMillis();
		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		after = System.currentTimeMillis();

   		Console.out.println("Type checked in " + (double)(after-before)/1000 + " secs. ");
		Console.out.println("There were " + TypeChecker.getWarningCount() + " warnings");
		TypeChecker.printErrors(Console.out);
		assertEquals("Type check errors", 0, TypeChecker.getErrorCount());

		ClassInterpreter interpreter = new ClassInterpreter(classes);
		CommandReader reader = new ClassCommandReader(interpreter, "");
		boolean OK = reader.assertFile(new File(assertions));
		assertEquals("Execution errors", true, OK);
	}

	public void test_Enigma() throws Exception
	{
		process("Enigma.vpp", "Enigma.assert");
	}

	public void test_POP3() throws Exception
	{
		process("POP3.vpp", "POP3.assert");
	}

	public void test_Factorial() throws Exception
	{
		process("factorial.vpp", "factorial.assert");
	}
}
