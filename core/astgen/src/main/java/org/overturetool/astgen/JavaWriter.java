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
import java.util.Set;


import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.NumericType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;

abstract public class JavaWriter extends LanguageWriter
{
	public JavaWriter()
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

		String file = dir.getPath() + File.separator + name + ".java";
		output = new PrintWriter(new FileWriter(file));
		System.out.print("Creating " + file + "... ");
		writeHeader();
	}

	private void writeHeader()
	{
		output.println("//");
		output.println("// Created automatically by VDMJ ASTgen. DO NOT EDIT.");
		output.println("// " + new Date());
		output.println("//\n");
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

	protected String methodName(String stem, Field f)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(stem.equals("has") ? "Boolean" : javaType(f.type));
		sb.append(' ');
		sb.append(stem);
		boolean upper = true;

		for (char c: f.tag.toCharArray())
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

	protected void importsFor(Type type, Set<String> imports, AbstractTree ast)
	{
		if (type instanceof OptionalType)
		{
			OptionalType ot = (OptionalType)type;
			importsFor(ot.type, imports, ast);
		}
		else if (type.isSeq())
		{
			SeqType st = type.getSeq();

			if (!(st.seqof instanceof CharacterType))
			{
				imports.add("java.util.List");
			}

			importsFor(st.seqof, imports, ast);
		}
		else if (tree.kind != Kind.INTF && type instanceof InvariantType)
		{
			TypeDefinition td = tree.findDefinition(type);

			imports.add(ast.basePkg + "." + Kind.INTF.subpkg +
				"." + ast.root + td.name + Kind.INTF.extension);
		}
	}

	protected String javaType(Type type)
	{
		if (type instanceof OptionalType)
		{
			OptionalType ot = (OptionalType)type;
			return javaType(ot.type);
		}
		else if (type.isSeq())
		{
			SeqType st = type.getSeq();

			if (st.seqof instanceof CharacterType)
			{
				return "String";
			}
			else
			{
				return "List<" + javaType(st.seqof) + ">";
			}
		}
		else if (type instanceof BooleanType)
		{
			return "Boolean";
		}
		else if (type instanceof CharacterType)
		{
			return "Character";
		}
		else if (type instanceof RealType)
		{
			return "Double";
		}
		else if (type instanceof NumericType)
		{
			return "Long";
		}
		else
		{
			return type.location.module + type.toString();
		}
	}

	protected String getInheritedPackage(LexNameToken name, Kind kind)
	{
		if (tree.isDerived())
		{
			String td = tree.getInheritedTypeName(name, kind);

			if (td != null)
			{
				return tree.derivedFrom().basePkg +
							"." + kind.subpkg + "." + td;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	protected String getPackage(LexNameToken name, Kind kind)
	{
		return tree.basePkg + "." + kind.subpkg + "." + tree.getKindName(name, kind);
	}
}
