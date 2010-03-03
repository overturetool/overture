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

import java.net.URL;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class CharsetTest extends VDMTestCase
{
	private void process(String resource, String charset)
	{
		Console.out.println("Processing " + resource + "...");

		URL rurl = getClass().getResource("/charsets/" + resource);
		String file = rurl.getPath();

		long before = System.currentTimeMillis();
		ClassList classes = parseClasses(file, charset);
		long after = System.currentTimeMillis();

		Console.out.println("Parsed " + classes.size() + " classes in " +
   			(double)(after-before)/1000 + " secs. ");

		before = System.currentTimeMillis();
		TypeChecker typeChecker = new ClassTypeChecker(classes);
		typeChecker.typeCheck();
		after = System.currentTimeMillis();

   		Console.out.println("Type checked in " + (double)(after-before)/1000 + " secs. ");
		Console.out.println("There were " + TypeChecker.getWarningCount() + " warnings");
		assertEquals("Type check errors", 0, TypeChecker.getErrorCount());
	}

	public void test_Dvorak()
	{
		process("Dvorak.vpp", "UTF-8");
	}

	public void test_JapaneseUtf8()
	{
		process("Japanese_UTF8.vpp", "UTF-8");
	}

	public void test_Shift_JIS()
	{
		process("Shift_JIS.vpp", "SJIS");
	}
}
