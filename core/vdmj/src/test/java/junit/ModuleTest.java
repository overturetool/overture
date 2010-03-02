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

import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.ModuleReader;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

import junit.framework.TestCase;

public class ModuleTest extends TestCase
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

	private void process(String resource)
	{
		Console.out.println("Processing " + resource + "...");

		URL rurl = getClass().getResource("/modtest/" + resource);
		String file = rurl.getPath();

		long before = System.currentTimeMillis();
		LexTokenReader ltr = new LexTokenReader(new File(file), Dialect.VDM_SL);
		ModuleReader mr = new ModuleReader(ltr);
		ModuleList modules = new ModuleList();
		modules.addAll(mr.readModules());
		long after = System.currentTimeMillis();
		Console.out.println("Parsed " + modules.size() + " modules in " +
   			(double)(after-before)/1000 + " secs. ");
		mr.printErrors(Console.out);
		mr.printWarnings(Console.out);
		assertEquals("Parse errors", 0, mr.getErrorCount());

		before = System.currentTimeMillis();
		TypeChecker typeChecker = new ModuleTypeChecker(modules);
		typeChecker.typeCheck();
		after = System.currentTimeMillis();
   		Console.out.println("Type checked in " + (double)(after-before)/1000 + " secs. ");
		Console.out.println("There were " + TypeChecker.getWarningCount() + " warnings");
		TypeChecker.printErrors(Console.out);
		assertEquals("Type check errors", 0, TypeChecker.getErrorCount());
	}

	public void testRailway()
	{
		process("railway.vdm");
	}

	public void testCSKExample()
	{
		process("cskexample.vdm");
	}

	public void testBar()
	{
		process("bar.vdm");
	}

	public void testNDB()
	{
		process("ndb.vdm");
	}

	public void testLoose()
	{
		process("loose.vdm");
	}

	public void testSoccer()
	{
		process("soccer.vdm");
	}

	public void testDLModule()
	{
		process("dlmodule.vdm");
	}

	public void testSimulator()
	{
		process("simulator1.vdm");
	}

	public void testGraphEd()
	{
		process("graph-ed.vdm");
	}

	public void testProg()
	{
		process("proglang.vdm");
	}

	public void testMetro()
	{
		process("metro.vdm");
	}

	public void testTelephone()
	{
		process("telephone.vdm");
	}

	public void testSAFER()
	{
		process("SAFER.vdm");
	}

	public void testExpress()
	{
		process("express.vdm");
	}
}
