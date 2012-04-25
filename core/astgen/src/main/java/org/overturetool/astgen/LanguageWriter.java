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
import java.io.IOException;
import java.io.PrintWriter;


import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.TypeSet;

public abstract class LanguageWriter
{
	protected static final String INDENT = "    ";

	protected File dir;
	protected AbstractTree tree;

	protected PrintWriter output;

	public LanguageWriter()
	{
		// :-)
	}

	public void setDetails(File dir, AbstractTree tree)
	{
		this.dir = packageDir(dir, tree);
		this.tree = tree;
	}

	private File packageDir(File d, AbstractTree t)
	{
		return new File(d.getPath() + File.separator + t.getPackageDir());
	}

	public static LanguageWriter factory(String language, Kind kind)
	{
		if (language.equals("java"))
		{
			switch (kind)
			{
				case IMPL: return new JavaImplWriter();
				case INTF: return new JavaIntfWriter();
			}
		}
		else if (language.equals("vdm"))
		{
			switch (kind)
			{
				case IMPL: return new VDMImplWriter();
				case INTF: return new VDMIntfWriter();
			}
		}
		else
		{
			System.err.println("Unknown language: " + language);
			System.exit(1);
		}

		return null;
	}


	protected String getInheritedName(LexNameToken name, Kind kind)
	{
		if (tree.isDerived())
		{
			String td = tree.getInheritedTypeName(name, kind);

			if (td != null)
			{
				return td;
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

	protected abstract void open(String name) throws IOException;
	protected abstract void close();

	public abstract void createAbstractInterface(LexNameToken name, LexNameToken parent) throws IOException;
	public abstract void createRecordInterface(RecordType rt, LexNameToken parent) throws IOException;
	public abstract void createQuoteEnumeration(LexNameToken name, TypeSet types, LexNameToken parent) throws IOException;
}
