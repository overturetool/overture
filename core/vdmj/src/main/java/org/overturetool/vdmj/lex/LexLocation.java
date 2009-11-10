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

package org.overturetool.vdmj.lex;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * A class to hold the location of a token.
 */

public class LexLocation implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** A collection of all LexLocation objects. */
	private static List<LexLocation> allLocations = new Vector<LexLocation>();
	/** True if the location is executable. */
	private boolean executable = false;

	/** The filename of the token. */
	public final File file;
	/** The module/class name of the token. */
	public final String module;
	/** The line number of the start of the token. */
	public final int startLine;
	/** The character position of the start of the token. */
	public final int startPos;
	/** The last line of the token. */
	public final int endLine;
	/** The character position of the end of the token. */
	public final int endPos;

	/** The number of times the location has been executed. */
	public long hits = 0;

	/**
	 * Create a location with the given fields.
	 */

	public LexLocation(File file, String module,
		int startLine, int startPos, int endLine, int endPos)
	{
		this.file = file;
		this.module = module;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;

		allLocations.add(this);
	}

	/**
	 * Create a default location.
	 */

	public LexLocation()
	{
		this(new File("?"), "?", 0, 0, 0, 0);
	}

	@Override
	public String toString()
	{
		if (file.equals("?"))
		{
			return "";		// Default LexLocation has no location string
		}
		else if (module == null || module.equals(""))
		{
			return "in '" + file + "' at line " + startLine + ":" + startPos;
		}
		else
		{
			return "in '" + module + "' (" + file + ") at line " + startLine + ":" + startPos;
		}
	}

	public String toShortString()
	{
		if (file.equals("?"))
		{
			return "";		// Default LexLocation has no location string
		}
		else
		{
			return "at " + startLine + ":" + startPos;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof LexLocation)
		{
			LexLocation lother = (LexLocation)other;

			return file.equals(lother.file) &&
					module.equals(lother.module) &&
					startLine == lother.startLine;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return file.hashCode() + module.hashCode() + startLine;
	}

	public void executable(boolean exe)
	{
		executable = exe;
	}

	public void hit()
	{
		if (executable) hits++;
	}

	public static void clearLocations()
	{
		for (LexLocation loc: allLocations)
		{
			loc.hits = 0;
		}
	}

	public static void resetLocations()
	{
		allLocations = new Vector<LexLocation>();
	}

	public static List<Integer> getHitList(File file)
	{
		List<Integer> hits = new Vector<Integer>();

		for (LexLocation l: allLocations)
		{
			if (l.hits > 0 && l.file.equals(file))
			{
				hits.add(l.startLine);
			}
		}

		return hits;
	}

	public static List<Integer> getMissList(String file)
	{
		List<Integer> misses = new Vector<Integer>();

		for (LexLocation l: allLocations)
		{
			if (l.hits == 0 && l.file.equals(file))
			{
				misses.add(l.startLine);
			}
		}

		return misses;
	}

	public static List<Integer> getSourceList(File file)
	{
		List<Integer> lines = new Vector<Integer>();

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.file.equals(file))
			{
				lines.add(l.startLine);
			}
		}

		return lines;
	}

	public static List<LexLocation> getHitLocations(File file)
	{
		List<LexLocation> hits = new Vector<LexLocation>();

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.hits > 0 && l.file.equals(file))
			{
				hits.add(l);
			}
		}

		return hits;
	}

	public static List<LexLocation> getMissLocations(String file)
	{
		List<LexLocation> misses = new Vector<LexLocation>();

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.hits == 0 && l.file.equals(file))
			{
				misses.add(l);
			}
		}

		return misses;
	}
}
