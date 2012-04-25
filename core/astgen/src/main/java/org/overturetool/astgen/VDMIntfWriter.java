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

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;

public class VDMIntfWriter extends VDMWriter
{
	public VDMIntfWriter()
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

		output.print("class " + aname);

		if (parent != null)
		{
			output.println(" is subclass of " + pname);
		}
		else
		{
			output.println();
		}

		output.println(INDENT + "-- Abstract");
		output.println("end " + aname);

		close();
	}

	@Override
	public void createQuoteEnumeration(
		LexNameToken name, TypeSet types, LexNameToken parent)
		throws IOException
	{
		String qname = tree.getKindName(name, Kind.INTF);
		String iname = tree.getKindName(parent, Kind.INTF);

		open(qname);

		output.println("class " + qname + " is subclass of " + iname);
		output.println("values");

		for (Type t: types)
		{
			QuoteType qt = (QuoteType)t;
			output.print(INDENT + "public " + qt.value + " = ");
			output.print("new " + qname + "(\"" + qt.value + "\");\n");
		}

		output.println();
		output.println("instance variables");
		output.println(INDENT + "public name:[seq of char] := nil;");
		output.println();
		output.println("operations");

		// Constructor ought to be private, but VDMTools can't cope.

		output.println(INDENT + "public " + qname + ": seq of char ==> " + qname);
		output.println(INDENT + qname + "(n) == name := n;");
		output.println("\nend " + qname);

		close();
	}

	@Override
	public void createRecordInterface(RecordType rt, LexNameToken parent)
		throws IOException
	{
		String name = tree.getKindName(rt.name, Kind.INTF);
		String pname = tree.getKindName(parent, Kind.INTF);
		String iname = this.getInheritedName(rt.name, Kind.INTF);

		open(name);

		output.print("class " + name);

		if (pname != null && iname == null)
		{
			output.println(" is subclass of " + pname);
		}
		else if (pname == null && iname != null)
		{
			output.println(" is subclass of " + iname);
		}
		else if (pname != null && iname != null)
		{
			output.println(" is subclass of " + pname + ", " + iname);
		}
		else
		{
			output.println();
		}

		boolean empty = true;
		output.println("operations");

		for (Field f: rt.fields)
		{
			if (f.type instanceof OptionalType)
			{
				output.print(operationName("has", f));
				output.println(" is subclass responsibility;\n");
			}

			output.print(operationName("get", f));
			output.println(" is subclass responsibility;\n");

			empty = false;
		}

		if (empty)
		{
			output.println("-- empty");
		}

		output.print("end " + name);
		close();
	}
}
