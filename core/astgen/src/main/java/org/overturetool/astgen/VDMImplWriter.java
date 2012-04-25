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
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.TypeSet;

public class VDMImplWriter extends VDMWriter
{
	private static final String PARAM = "p_";
	private static final String VAR = "iv_";

	public VDMImplWriter()
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

		output.print("class " + aname);

		if (parent != null)
		{
			output.println(" is subclass of " + pname);
		}
		else
		{
			output.println();
		}

		output.println(INDENT + "-- empty");
		output.println("end " + aname);

		close();
	}

	@Override
	public void createQuoteEnumeration(
		LexNameToken name, TypeSet types, LexNameToken parent)
		throws IOException
	{
		String qname = tree.getKindName(name, Kind.IMPL);
		String iname = tree.getKindName(name, Kind.INTF);

		open(qname);

		output.println("class " + qname + " is subclass of " + iname);

		// Values inherited from iname's copy

		output.println("\nend " + qname);

		close();
	}

	@Override
	public void createRecordInterface(RecordType rt, LexNameToken parent)
		throws IOException
	{
		String name = tree.getKindName(rt.name, Kind.IMPL);
		String iname = tree.getKindName(rt.name, Kind.INTF);
		String ename = getInheritedName(rt.name, Kind.IMPL);

		open(name);

		output.print("class " + name);

		if (iname != null && ename == null)
		{
			output.println(" is subclass of " + iname);
		}
		else if (iname == null && ename != null)
		{
			output.println(" is subclass of " + ename);
		}
		else if (iname != null && ename != null)
		{
			output.println(" is subclass of " + iname + ", " + ename);
		}
		else
		{
			output.println();
		}

		if (!rt.fields.isEmpty())
		{
    		output.println("instance variables");

    		for (Field f: rt.fields)
    		{
    			output.println(INDENT + "private " + varName(f.tag) + ":" + getVdmType(f.type, true) + ";");
    		}
		}

		RecordType superclass = tree.getSuperClass(rt.name);
		if (!rt.fields.isEmpty()) output.println();
		output.println("operations");

		output.print(INDENT + "public " + name + ": ");
		String sep = "";

		if (superclass != null)
		{
			for (Field f: superclass.fields)
			{
				output.print(sep);
				output.print(getVdmType(f.type, true));
				sep = " * ";
			}
		}

		for (Field f: rt.fields)
		{
			output.print(sep);
			output.print(getVdmType(f.type, true));
			sep = " * ";
		}

		if (sep.equals(""))		// No params at all
		{
			output.print("()");
		}

		output.println(" ==> " + name);
		output.print(INDENT + name + "(");
		sep = "";

		if (superclass != null)
		{
			for (Field f: superclass.fields)
			{
				output.print(sep);
				output.print(paramName(f.tag));
				sep = ", ";
			}
		}

		for (Field f: rt.fields)
		{
			output.print(sep);
			output.print(paramName(f.tag));
			sep = ", ";
		}

		output.println(") ==");
		output.println(INDENT + "(");

		if (superclass != null)
		{
			output.print(INDENT + INDENT +
				tree.derivedFrom().getKindName(superclass.name, Kind.IMPL) + "(");
			sep = "";

			for (Field f: superclass.fields)
			{
				output.print(sep);
				output.print(paramName(f.tag));
				sep = ", ";
			}

			output.println(");");
		}

		for (Field f: rt.fields)
		{
			output.println(INDENT + INDENT +
				varName(f.tag) + " := " + paramName(f.tag) + ";");
		}

		if (superclass == null && rt.fields.isEmpty())
		{
			output.println(INDENT + INDENT + "skip;");
		}

		output.println(INDENT + ");");

		for (Field f: rt.fields)
		{
			if (f.type instanceof OptionalType)
			{
				output.println();
				output.print(operationName("has", f));
				output.println(" return (" + varName(f.tag) + " = nil);");
			}

			output.println();
			output.print(operationName("get", f));
			output.println(" return " + varName(f.tag) + ";");
		}

		output.print("\nend " + name);
		close();
	}

	private String varName(String tag)
	{
		return VAR + tag;
	}


	private String paramName(String tag)
	{
		return PARAM + tag;
	}
}
