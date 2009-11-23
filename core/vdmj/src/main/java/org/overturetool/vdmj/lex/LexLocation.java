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
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

/**
 * A class to hold the location of a token.
 */

public class LexLocation implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** A collection of all LexLocation objects. */
	private static List<LexLocation> allLocations = new Vector<LexLocation>();

	/** A map of f/op/class names to their lexical span, for coverage. */
	private static Map<LexNameToken, LexLocation> nameSpans =
		new HashMap<LexNameToken, LexLocation>();

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

	public boolean within(LexLocation span)
	{
		return
			(startLine > span.startLine ||
				(startLine == span.startLine && startPos >= span.startPos)) &&
			(startLine <= span.endLine ||
				(startLine == span.endLine && startPos < span.endPos)) &&
			file.equals(span.file);
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
		nameSpans =	new HashMap<LexNameToken, LexLocation>();
	}

	public static void clearAfter(File file, int linecount, int charpos)
	{
		// Called from the LexTokenReader's pop method, to remove any
		// locations "popped". We assume any pushes are on the end of
		// the vector.

		ListIterator<LexLocation> it =
			allLocations.listIterator(allLocations.size());

		while (it.hasPrevious())
		{
			LexLocation l = it.previous();

			if (!l.file.equals(file) ||
				l.startLine < linecount ||
				l.startPos < charpos)
			{
				break;
			}
			else
			{
				it.remove();
			}
		}
	}

	public static void addSpan(LexNameToken name, LexToken upto)
	{
		LexLocation span = new LexLocation(
			name.location.file,
			name.location.module,
			name.location.startLine,
			name.location.startPos,
			upto.location.endLine,
			upto.location.endPos);

		nameSpans.put(name, span);
	}

	public static LexNameList getSpanNames(File filename)
	{
		LexNameList list = new LexNameList();

		for (LexNameToken name: nameSpans.keySet())
		{
			LexLocation span = nameSpans.get(name);

			if (span.file.equals(filename))
			{
				list.add(name);
			}
		}

		return list;
	}

	public static float getSpanPercent(LexNameToken name)
	{
		int hits = 0;
		int misses = 0;
		LexLocation span = nameSpans.get(name);

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.within(span))
			{
				if (l.hits > 0)
    			{
    				hits++;
    			}
    			else
    			{
    				misses++;
    			}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float)((1000 * hits)/sum/10);		// NN.N%
	}

	public static long getSpanCalls(LexNameToken name)
	{
		// The assumption is that the first executable location in
		// the span for the name is hit as many time as the span is called.

		LexLocation span = nameSpans.get(name);

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.within(span))
			{
				return l.hits;
			}
		}

		return 0;
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

	public static List<Integer> getMissList(File file)
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

	public static Map<Integer, List<LexLocation>> getHitLocations(File file)
	{
		Map<Integer, List<LexLocation>> map =
				new HashMap<Integer, List<LexLocation>>();

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.hits > 0 && l.file.equals(file))
			{
				List<LexLocation> list = map.get(l.startLine);

				if (list == null)
				{
					list = new Vector<LexLocation>();
					map.put(l.startLine, list);
				}

				list.add(l);
			}
		}

		return map;
	}

	public static float getHitPercent(File file)
	{
		int hits = 0;
		int misses = 0;

		for (LexLocation l: allLocations)
		{
			if (l.file.equals(file) && l.executable)
			{
				if (l.hits > 0)
    			{
    				hits++;
    			}
    			else
    			{
    				misses++;
    			}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float)((1000 * hits)/sum/10);		// NN.N%
	}

	public static Map<Integer, List<LexLocation>> getMissLocations(File file)
	{
		Map<Integer, List<LexLocation>> map =
				new HashMap<Integer, List<LexLocation>>();

		for (LexLocation l: allLocations)
		{
			if (l.executable && l.hits == 0 && l.file.equals(file))
			{
				List<LexLocation> list = map.get(l.startLine);

				if (list == null)
				{
					list = new Vector<LexLocation>();
					map.put(l.startLine, list);
				}

				list.add(l);
			}
		}

		return map;
	}
}
