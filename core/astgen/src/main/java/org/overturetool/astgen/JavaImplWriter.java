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

public class JavaImplWriter extends JavaWriter
{
	public JavaImplWriter()
	{
		super();
	}

	@Override
	public void createAbstractInterface(LexNameToken name, LexNameToken parent)
		throws IOException
	{
		String aname = tree.getKindName(name, Kind.IMPL);
		String pname = tree.getKindName(parent, Kind.IMPL);

		open(aname);

		output.println("package " + tree.getPackage() + ";");
		output.println();
		output.print("abstract public class " + aname);

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
		String ename = tree.getKindName(name, Kind.IMPL);

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
		String typename = tree.getKindName(rt.name, Kind.IMPL);
		String iname = tree.getKindName(rt.name, Kind.INTF);

		open(typename);

		output.println("package " + tree.getPackage() + ";");
		output.println();

		RecordType superclass = tree.getSuperClass(rt.name);
		Set<String> imports = new HashSet<String>();

		if (superclass != null)
		{
			for (Field f: superclass.fields)
			{
				importsFor(f.type, imports, tree.derivedFrom());
			}
		}

		for (Field f: rt.fields)
		{
			importsFor(f.type, imports, tree);
		}

		String ename = getInheritedName(rt.name, Kind.IMPL);
		String epkg  = getInheritedPackage(rt.name, Kind.IMPL);

		if (epkg != null)
		{
			imports.add(epkg);
		}

		if (iname != null)
		{
			imports.add(getPackage(rt.name, Kind.INTF));
		}

		if (!imports.isEmpty())
		{
			for (String imp: imports)
			{
				output.println("import " + imp + ";");
			}

			output.println();
		}

		output.print("public class " + typename);

		if (ename != null)
		{
			output.print(" extends " + ename);
		}

		if (iname != null)
		{
			output.print(" implements " + iname);
		}

		output.println();
		output.println("{");

		for (Field f: rt.fields)
		{
			output.println(INDENT + "private " + javaType(f.type) + " " + f.tag + ";");
		}

		if (!rt.fields.isEmpty()) output.println();
		output.print(INDENT + "public " + typename + "(");
		String sep = "";

		if (superclass != null)
		{
			for (Field f: superclass.fields)
			{
				output.print(sep);
				output.print(javaType(f.type) + " " + f.tag);
				sep = ", ";
			}
		}

		for (Field f: rt.fields)
		{
			output.print(sep);
			output.print(javaType(f.type) + " " + f.tag);
			sep = ", ";
		}

		output.println(")");
		output.println(INDENT + "{");
		boolean empty = true;

		if (superclass != null)
		{
			output.print(INDENT + INDENT + "super(");
			sep = "";

			for (Field f: superclass.fields)
			{
				output.print(sep);
				output.print(f.tag);
				sep = ", ";
			}

			output.println(");");
			empty = false;
		}

		for (Field f: rt.fields)
		{
			output.println(INDENT + INDENT + "this." + f.tag + " = " + f.tag + ";");
			empty = false;
		}

		if (empty)
		{
			output.println(INDENT + INDENT + "// empty");
		}

		output.println(INDENT + "}");

		for (Field f: rt.fields)
		{
			if (f.type instanceof OptionalType)
			{
				output.println();
				output.print(INDENT);
				output.print("public ");
				output.print(methodName("has", f));
				output.println("()");
				output.println(INDENT + "{");
				output.println(INDENT + INDENT + "return (" + f.tag + " != null);");
				output.println(INDENT + "}");
			}

			output.println();
			output.print(INDENT);
			output.print("public ");
			output.print(methodName("get", f));
			output.println("()");
			output.println(INDENT + "{");
			output.println(INDENT + INDENT + "return " + f.tag + ";");
			output.println(INDENT + "}");
		}

		output.println("}");
		close();
	}
}
