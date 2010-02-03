/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.astgen;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;

public class JavaIntfWriter extends JavaWriter
{
	public JavaIntfWriter()
	{
		super();
	}

	@Override
	public void createAbstractInterface(LexNameToken name, LexNameToken parent)
		throws IOException
	{
		String aname = tree.getKindName(name, Kind.INTF);
		String pname = tree.getKindName(parent, Kind.INTF);

		open(aname);

		output.println("package " + tree.getPackage() + ";");
		output.println();
		output.print("public interface " + aname);

		if (parent != null)
		{
			output.println(" extends " + pname);
		}
		else
		{
			output.println();
		}

		output.println("{");
		output.println("    // empty");
		output.println("}");

		close();
	}

	@Override
	public void createQuoteEnumeration(
		LexNameToken name, TypeSet types, LexNameToken parent)
		throws IOException
	{
		String ename = tree.getKindName(name, Kind.INTF);

		open(ename);

		output.println("package " + tree.getPackage() + ";");
		output.println();
		output.println("public enum " + ename);

		output.print("{");
		String sep = "";
		boolean empty = true;

		for (Type t: types)
		{
			QuoteType qt = (QuoteType)t;
			output.println(sep);
			output.print(INDENT + qt.value);
			empty = false;
			sep = ",";
		}

		output.println();

		if (empty)
		{
			output.print(INDENT);
			output.println("// empty");
		}

		output.println("}");

		close();
	}

	@Override
	public void createRecordInterface(RecordType rt, LexNameToken parent)
		throws IOException
	{
		String typename = tree.getKindName(rt.name, Kind.INTF);
		String pname = tree.getKindName(parent, Kind.INTF);

		open(typename);

		output.println("package " + tree.getPackage() + ";");
		output.println();

		Set<String> imports = new HashSet<String>();

		for (Field f: rt.fields)
		{
			importsFor(f.type, imports, tree);
		}

		String iname = getInheritedName(rt.name, Kind.INTF);
		String ipkg  = getInheritedPackage(rt.name, Kind.INTF);

		if (ipkg != null)
		{
			imports.add(ipkg);
		}

		if (!imports.isEmpty())
		{
			for (String imp: imports)
			{
				output.println("import " + imp + ";");
			}

			output.println();
		}

		output.print("public interface " + typename);

		if (pname != null && iname == null)
		{
			output.println(" extends " + pname);
		}
		else if (pname == null && iname != null)
		{
			output.println(" extends " + iname);
		}
		else if (pname != null && iname != null)
		{
			output.println(" extends " + pname + ", " + iname);
		}
		else
		{
			output.println();
		}

		output.println("{");
		boolean empty = true;

		for (Field f: rt.fields)
		{
			if (f.type instanceof OptionalType)
			{
				output.print(INDENT);
				output.print("public ");
				output.print(methodName("has", f));
				output.println("();");
			}

			output.print(INDENT);
			output.print("public ");
			output.print(methodName("get", f));
			output.println("();");

			empty = false;
		}

		if (empty)
		{
			output.print(INDENT);
			output.println("// empty");
		}

		output.println("}");
		close();
	}
}
