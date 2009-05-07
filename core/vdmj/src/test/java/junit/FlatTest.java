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

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.DefinitionReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

import junit.framework.TestCase;

public class FlatTest extends TestCase
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

	private void process(String resource) throws Exception
	{
		Console.out.println("Processing " + resource + "...");

		URL rurl = getClass().getResource("/flattest/" + resource);
		String file = rurl.getPath();

		LexTokenReader ltr = new LexTokenReader(new File(file), Dialect.VDM_SL);
		DefinitionReader dr = new DefinitionReader(ltr);
		DefinitionList definitions = new DefinitionList();

		long before = System.currentTimeMillis();
		definitions.addAll(dr.readDefinitions());
		long after = System.currentTimeMillis();
		Console.out.println("Parsed " + definitions.size() + " definitions in " +
   			(double)(after-before)/1000 + " secs. ");
		dr.printErrors(Console.out);
		assertEquals("Parse errors", 0, dr.getErrorCount());

		// TypeChecker typeChecker = new FlatTypeChecker(definitions, false);
		ModuleList modules = new ModuleList();
		modules.add(new Module(definitions));
		TypeChecker typeChecker = new ModuleTypeChecker(modules);

		before = System.currentTimeMillis();
		typeChecker.typeCheck();
		after = System.currentTimeMillis();
   		Console.out.println("Type checked in " + (double)(after-before)/1000 + " secs. ");
		Console.out.println("There were " + TypeChecker.getWarningCount() + " warnings");
		TypeChecker.printErrors(Console.out);
		assertEquals("Type check errors", 0, TypeChecker.getErrorCount());
	}

	public void testDFDExample() throws Exception
	{
		process("dfdexample.def");
	}

	public void testNDB() throws Exception
	{
		process("ndb.def");
	}

	public void testNewSpeak() throws Exception
	{
		process("newspeak.def");
	}

	public void testSTV() throws Exception
	{
		process("stv.def");
	}

	public void testACS() throws Exception
	{
		process("acs.def");
	}

	public void testMAA() throws Exception
	{
		process("maa.def");
	}

	public void testCrossword() throws Exception
	{
		process("crossword.def");
	}

	public void testRealm() throws Exception
	{
		process("realm.def");
	}

	public void testSort() throws Exception
	{
		process("sort.def");
	}

	public void testADT() throws Exception
	{
		process("adt.def");
	}

	public void testLibrary() throws Exception
	{
		process("library.def");
	}

	public void testPlanner() throws Exception
	{
		process("planner.def");
	}

	public void testCM() throws Exception
	{
		process("cmflat.def");
	}

	public void testWorldCup() throws Exception
	{
		process("worldcup.def");
	}

	public void testGeneral() throws Exception
	{
		process("general.def");
	}
}
