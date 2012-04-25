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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;

abstract public class VDMWriter extends LanguageWriter
{
	public VDMWriter()
	{
		super();
	}

	@Override
	protected void open(String name) throws IOException
	{
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		String file = dir.getPath() + File.separator + name + ".vdmpp";
		output = new PrintWriter(new FileWriter(file));
		System.out.print("Creating " + file + "... ");
		writeHeader();
	}

	private void writeHeader()
	{
		output.println("--");
		output.println("-- Created automatically by VDMJ ASTgen. DO NOT EDIT.");
		output.println("-- " + new Date());
		output.println("--\n");
	}

	@Override
	protected void close()
	{
		output.close();
		System.out.println("done.");
	}

	@Override
	abstract public void createAbstractInterface(LexNameToken name, LexNameToken parent)
		throws IOException;

	@Override
	abstract public void createQuoteEnumeration(LexNameToken name, TypeSet types, LexNameToken parent)
		throws IOException;

	@Override
	abstract public void createRecordInterface(RecordType rt, LexNameToken parent)
		throws IOException;

	protected String operationName(String stem, Field f)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(INDENT);
		sb.append("public ");
		sb.append(stem);
		sb.append(fixName(f.tag));
		sb.append(": () ==> ");
		sb.append(stem.equals("has") ? "bool" : getVdmType(f.type, false));
		sb.append("\n");
		sb.append(INDENT);
		sb.append(stem);
		sb.append(fixName(f.tag));
		sb.append("() ==");

		return sb.toString();
	}

	private String fixName(String tag)
	{
		StringBuilder sb = new StringBuilder();
		boolean upper = true;

		for (char c: tag.toCharArray())
		{
			if (upper)
			{
				sb.append(Character.toUpperCase(c));
				upper = false;
			}
			else if (c == '_')
			{
				upper = true;
			}
			else
			{
				sb.append(c);
			}
		}

		return sb.toString();
	}

	protected String getVdmType(Type type, boolean withOptional)
	{
		if (type instanceof OptionalType)
		{
			OptionalType ot = (OptionalType)type;

			if (!withOptional)
			{
				return getVdmType(ot.type, withOptional);
			}
    		else
    		{
    			return "[" + getVdmType(ot.type, false) + "]";
    		}
    	}
		else if (type instanceof SeqType)
		{
			SeqType st = (SeqType)type;

			if (st.seqof instanceof CharacterType)
			{
				return "seq of char";
			}
			else
			{
				return "seq of " + getVdmType(st.seqof, withOptional);
			}
		}
		else if (type instanceof SetType)
		{
			SetType st = (SetType)type;
			return "set of " + getVdmType(st.setof, withOptional);
		}
		else if (type instanceof BooleanType)
		{
			return "bool";
		}
		else if (type instanceof CharacterType)
		{
			return "char";
		}
		else if (type instanceof RealType)
		{
			return "real";
		}
		else if (type instanceof IntegerType)
		{
			return "int";
		}
		else if (type instanceof NaturalType)
		{
			return "nat";
		}
		else if (type instanceof NaturalOneType)
		{
			return "nat1";
		}
		else
		{
			return type.location.module + type.toString();
		}
	}
}
