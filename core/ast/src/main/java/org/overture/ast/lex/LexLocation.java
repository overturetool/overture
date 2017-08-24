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

package org.overture.ast.lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.ExternalNode;

/**
 * A class to hold the location of a token.
 */

public class LexLocation implements Serializable, ExternalNode, ILexLocation
{
	@Override
	public LexLocation clone()
	{
		LexLocation location = new LexLocation(file, module, startLine, startPos, endLine, endPos, startOffset, endOffset);
		location.hits = hits;
		location.executable = executable;
		return location;
	}

	public static boolean absoluteToStringLocation = true;

	private static final long serialVersionUID = 1L;

	/** A collection of all LexLocation objects. */
	private static List<LexLocation> allLocations = new Vector<LexLocation>();

	/** A unique map of LexLocation objects, for rapid searching. */
	private static Map<LexLocation, LexLocation> uniqueLocations = new HashMap<LexLocation, LexLocation>();

	/** A map of class names to their lexical span, for coverage. */
	private static Map<LexNameToken, LexLocation> nameSpans = new HashMap<LexNameToken, LexLocation>();// TODO


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

	public final int startOffset;

	public final int endOffset;

	/** The number of times the location has been executed. */
	public long hits = 0;

	/**
	 * Create a location with the given fields.
	 * 
	 * @param file
	 * @param module
	 * @param startLine
	 * @param startPos
	 * @param endLine
	 * @param endPos
	 * @param startOffset
	 * @param endOffset
	 */

	public LexLocation(File file, String module, int startLine, int startPos,
			int endLine, int endPos, int startOffset, int endOffset)
	{
		this.file = file;
		this.module = module;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;
		this.startOffset = startOffset;
		this.endOffset = endOffset;

		synchronized (allLocations)
		{
			allLocations.add(this);
			uniqueLocations.put(this, this);
		}
	}

	public LexLocation(String filePath, String module, int startLine,
			int startPos, int endLine, int endPos, int startOffset,
			int endOffset)
	{
		this.file = new File(filePath);
		this.module = module;
		this.startLine = startLine;
		this.startPos = startPos;
		this.endLine = endLine;
		this.endPos = endPos;
		this.startOffset = startOffset;
		this.endOffset = endOffset;

		synchronized (allLocations)
		{
			allLocations.add(this);
			uniqueLocations.put(this, this);
		}
	}

	/**
	 * Create a default location.
	 */

	public LexLocation()
	{
		this(new File("?"), "?", 0, 0, 0, 0, 0, 0);
	}

	@Override
	public boolean getExecutable()
	{
		return executable;
	}

	@Override
	public long getHits()
	{
		return hits;
	}

	@Override
	public void setHits(long hits)
	{
		this.hits = hits;
	}

	@Override
	public File getFile()
	{
		return file;
	}

	@Override
	public String getModule()
	{
		return module;
	}

	@Override
	public int getStartLine()
	{
		return startLine;
	}

	@Override
	public int getStartPos()
	{
		return startPos;
	}

	@Override
	public int getEndLine()
	{
		return endLine;
	}

	@Override
	public int getEndPos()
	{
		return endPos;
	}

	@Override
	public int getStartOffset()
	{
		return startOffset;
	}

	@Override
	public int getEndOffset()
	{
		return endOffset;
	}

	@Override
	public String toString()
	{
		if (file.getPath().equals("?"))
		{
			return ""; // Default LexLocation has no location string
		} else if (module == null || module.equals(""))
		{
			return "in '" + (absoluteToStringLocation ? file : file.getName())
					+ "' at line " + startLine + ":" + startPos;
		} else
		{
			return "in '" + module + "' ("
					+ (absoluteToStringLocation ? file : file.getName())
					+ ") at line " + startLine + ":" + startPos;
		}
	}

	public String toShortString()
	{
		if (file.getPath().equals("?"))
		{
			return ""; // Default LexLocation has no location string
		} else
		{
			return "at " + startLine + ":" + startPos;
		}
	}

	/**
	 * Method to resolve existing locations during de-serialise - as used during deep copy. This is to avoid problems
	 * with coverage.
	 */
	private Object readResolve() throws ObjectStreamException
	{
		LexLocation existing = uniqueLocations.get(this);

		if (existing == null)
		{
			return this;
		} else
		{
			return existing;
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ILexLocation)
		{
			ILexLocation lother = (ILexLocation) other;

			return startPos == lother.getStartPos()
					&& startLine == lother.getStartLine()
					&& module.equals(lother.getModule())
					&& file.equals(lother.getFile());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return file.hashCode() + module.hashCode() + startLine + startPos;
	}

	public boolean within(ILexLocation span)
	{
		return (startLine > span.getStartLine() || startLine == span.getStartLine()
				&& startPos >= span.getStartPos())
				&& (startLine <= span.getEndLine() || startLine == span.getEndLine()
						&& startPos < span.getEndPos())
				&& file.equals(span.getFile());
	}

	public void executable(boolean exe)
	{
		executable = exe;
	}

	public void hit()
	{
		if (executable)
		{
			hits++;
		}
	}

	public static void clearLocations()
	{
		synchronized (allLocations)
		{
			for (LexLocation loc : allLocations)
			{
				loc.hits = 0;
			}
		}
	}

	public static void resetLocations()
	{
		synchronized (allLocations)
		{
			allLocations = new Vector<LexLocation>();
		}

		synchronized (uniqueLocations)
		{
			uniqueLocations = new HashMap<LexLocation, LexLocation>();
		}

		// synchronized (locationToAstNode)
		// {
		// locationToAstNode = new Hashtable<LexLocation, INode>();
		// }
		//
		// synchronized (nameSpans)
		// {
		// nameSpans = new HashMap<LexNameToken, LexLocation>();
		// }
	}

	public static void clearAfter(File file, int linecount, int charpos)
	{
		// Called from the LexTokenReader's pop method, to remove any
		// locations "popped". We assume any pushes are on the end of
		// the vector.
		synchronized (allLocations)
		{
			ListIterator<LexLocation> it = allLocations.listIterator(allLocations.size());

			while (it.hasPrevious())
			{
				LexLocation l = it.previous();

				if (!l.file.equals(file) || l.startLine < linecount
						|| l.startLine == linecount && l.startPos < charpos)
				{
					break;
				} else
				{
					it.remove();
					uniqueLocations.remove(l);
				}
			}
		}
	}

	public static void addSpan(LexNameToken name, LexToken upto)
	{
		LexLocation span = new LexLocation(name.location.getFile(), name.location.getModule(), name.location.getStartLine(), name.location.getStartPos(), upto.location.getEndLine(), upto.location.getEndPos(), upto.location.getStartOffset(), upto.location.getEndOffset());

		nameSpans.put(name, span);
	}

	public static List<Integer> getSourceList(File file)
	{
		List<Integer> lines = new Vector<Integer>();
		int last = 0;

		synchronized (allLocations)
		{
			for (LexLocation l : allLocations)
			{
				if (l.executable && l.startLine != last && l.file.equals(file))
				{
					lines.add(l.startLine);
					last = l.startLine;
				}
			}
		}

		return lines;
	}

	public static List<LexLocation> getSourceLocations(File file)
	{
		List<LexLocation> locations = new Vector<LexLocation>();

		synchronized (allLocations)
		{
			for (LexLocation l : allLocations)
			{
				if (l.executable && l.file.equals(file))
				{
					locations.add(l);
				}
			}
		}

		return locations;
	}

	public static void mergeHits(File source, File coverage) throws IOException
	{
		List<LexLocation> locations = getSourceLocations(source);
		BufferedReader br = new BufferedReader(new FileReader(coverage));
		String line = br.readLine();

		while (line != null)
		{
			if (line.charAt(0) == '+')
			{
				// Hit lines are "+line from-to=hits"

				int s1 = line.indexOf(' ');
				int s2 = line.indexOf('-');
				int s3 = line.indexOf('=');

				int lnum = Integer.parseInt(line.substring(1, s1));
				int from = Integer.parseInt(line.substring(s1 + 1, s2));
				int to = Integer.parseInt(line.substring(s2 + 1, s3));
				int hits = Integer.parseInt(line.substring(s3 + 1));

				for (LexLocation l : locations) // Only executable locations
				{
					if (l.startLine == lnum && l.startPos == from
							&& l.endPos == to)
					{
						l.hits += hits;
						break;
					}
				}
			}

			line = br.readLine();
		}

		br.close();
	}

	public static List<ILexLocation> getAllLocations()
	{
		Vector<ILexLocation> tmp = new Vector<ILexLocation>();
		synchronized (allLocations) {
			tmp.addAll(allLocations);
		}
		return tmp;
	}
	
	public static Map<LexNameToken, ILexLocation> getNameSpans()
	{
		Map<LexNameToken, ILexLocation> tmp = new HashMap<>();
		synchronized (nameSpans) {
		for (Entry<LexNameToken, LexLocation> entry : nameSpans.entrySet()) {
			tmp.put(entry.getKey(), entry.getValue());
		}	
		}
		return tmp;
		
	}
}
