/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;

public class LexLocationUtils implements Serializable
{

	public static boolean absoluteToStringLocation = true;

	private static final long serialVersionUID = 1L;

	/** A collection of all LexLocation objects. */
	private static List<ILexLocation> allLocations = new Vector<>();

	/** A collection of all LexLocation objects to the AstNodes. */
	private static Map<ILexLocation, INode> locationToAstNode = new Hashtable<ILexLocation, INode>();

	/** A map of f/op/class names to their lexical span, for coverage. */
	private static Map<LexNameToken, ILexLocation> nameSpans = new HashMap<>();// TODO

	public static void clearLocations()
	{
		synchronized (allLocations)
		{
			for (ILexLocation loc : allLocations)
			{
				loc.setHits(0);
			}
		}
	}

	public static void resetLocations()
	{
		synchronized (allLocations)
		{
			allLocations = new Vector<>();
		}

		synchronized (locationToAstNode)
		{
			locationToAstNode = new Hashtable<ILexLocation, INode>();
		}
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

			ListIterator<ILexLocation> it = allLocations.listIterator(allLocations.size());

			while (it.hasPrevious())
			{
				ILexLocation l = it.previous();

				if (!l.getFile().equals(file) || l.getStartLine() < linecount
						|| l.getStartLine() == linecount
						&& l.getStartPos() < charpos)
				{
					break;
				} else
				{
					it.remove();
				}
			}
		}
	}

	public static void addSpan(LexNameToken name, LexToken upto)
	{
		LexLocation span = new LexLocation(name.location.getFile(), name.location.getModule(), name.location.getStartLine(), name.location.getStartPos(), upto.location.getEndLine(), upto.location.getEndPos(), upto.location.getStartOffset(), upto.location.getEndOffset());

		nameSpans.put(name, span);
	}

	public static LexNameList getSpanNames(File filename)
	{
		LexNameList list = new LexNameList();

		for (LexNameToken name : nameSpans.keySet())
		{
			ILexLocation span = nameSpans.get(name);

			if (span.getFile().equals(filename))
			{
				list.add(name);
			}
		}

		return list;
	}

	public static float getSpanPercent(ILexNameToken name)
	{
		int hits = 0;
		int misses = 0;
		ILexLocation span = null;

		synchronized (nameSpans)
		{
			span = nameSpans.get(name);
		}

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.within(span))
				{
					if (l.getHits() > 0)
					{
						hits++;
					} else
					{
						misses++;
					}
				}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float) (1000 * hits / sum) / 10; // NN.N%
	}

	public static long getSpanCalls(ILexNameToken name)
	{
		// The assumption is that the first executable location in
		// the span for the name is hit as many time as the span is called.

		ILexLocation span = null;

		synchronized (nameSpans)
		{
			span = nameSpans.get(name);
		}

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.within(span))
				{
					return l.getHits();
				}
			}
		}

		return 0;
	}

	public static List<Integer> getHitList(File file)
	{
		List<Integer> hits = new Vector<>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getHits() > 0 && l.getFile().equals(file))
				{
					hits.add(l.getStartLine());
				}
			}
		}

		return hits;
	}

	public static List<Integer> getMissList(File file)
	{
		List<Integer> misses = new Vector<>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getHits() == 0 && l.getFile().equals(file))
				{
					misses.add(l.getStartLine());
				}
			}
		}

		return misses;
	}

	public static List<Integer> getSourceList(File file)
	{
		List<Integer> lines = new Vector<>();
		int last = 0;

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getStartLine() != last
						&& l.getFile().equals(file))
				{
					lines.add(l.getStartLine());
					last = l.getStartLine();
				}
			}
		}

		return lines;
	}

	public static Map<Integer, List<ILexLocation>> getHitLocations(File file)
	{
		Map<Integer, List<ILexLocation>> map = new HashMap<>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getHits() > 0
						&& l.getFile().equals(file))
				{
					List<ILexLocation> list = map.get(l.getStartLine());

					if (list == null)
					{
						list = new Vector<>();
						map.put(l.getStartLine(), list);
					}

					list.add(l);
				}
			}
		}

		return map;
	}

	public static float getHitPercent(File file)
	{
		int hits = 0;
		int misses = 0;

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getFile().equals(file) && l.getExecutable())
				{
					if (l.getHits() > 0)
					{
						hits++;
					} else
					{
						misses++;
					}
				}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float) (1000 * hits / sum) / 10; // NN.N%
	}

	public static Map<Integer, List<ILexLocation>> getMissLocations(File file)
	{
		Map<Integer, List<ILexLocation>> map = new HashMap<>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getHits() == 0
						&& l.getFile().equals(file))
				{
					List<ILexLocation> list = map.get(l.getStartLine());

					if (list == null)
					{
						list = new Vector<>();
						map.put(l.getStartLine(), list);
					}

					list.add(l);
				}
			}
		}

		return map;
	}

	public static List<ILexLocation> getSourceLocations(File file)
	{
		List<ILexLocation> locations = new Vector<>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getFile().equals(file))
				{
					locations.add(l);
				}
			}
		}

		return locations;
	}

	public static void mergeHits(File source, File coverage) throws IOException
	{
		List<ILexLocation> locations = getSourceLocations(source);
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

				for (ILexLocation l : locations) // Only executable locations
				{
					if (l.getStartLine() == lnum && l.getStartPos() == from
							&& l.getEndPos() == to)
					{
						l.setHits(l.getHits() + hits);
						// l.hits += hits;
						break;
					}
				}
			}

			line = br.readLine();
		}

		br.close();
	}

	// FIXME we know this is never called a new solutions is needed
	public static void addAstNode(LexLocation location, INode node)
	{
		synchronized (locationToAstNode)
		{
			locationToAstNode.put(location, node);
		}
	}

	public static Map<ILexLocation, INode> getLocationToAstNodeMap()
	{
		return locationToAstNode;
	}

	public static List<ILexLocation> getAllLocations()
	{
		return allLocations;
	}

}
