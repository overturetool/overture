/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.utility.ast;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PExport;
import org.overture.ast.modules.PImport;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.internal.viewsupport.ImportsContainer;

/**
 * Class used by an editor to search the editor text for source code node locations. Used to find nodes in the source
 * code to sync with outline
 * 
 * @author kela
 */
public final class AstLocationSearcher extends DepthFirstAnalysisAdaptor
{

	private static boolean DEBUG_PRINT = false;

	/**
	 * Best match to the offset. This means that this node has a location where the offset is within
	 */
	private INode bestHit = null;
	/**
	 * Best alternative hit is a location which is abs close to the offset
	 */
	private INode bestAlternativeHit = null;
	/**
	 * Best alternative hit is a node which has a location which is abs close to the offset
	 */
	private ILexLocation bestAlternativeLocation;
	/**
	 * The offset used when searching for nodes within this location of the source code
	 */
	private int offSet;

	/**
	 * The source file to search
	 */
	private File sourceFile;

	private static final AstLocationSearcher seacher = new AstLocationSearcher();

	private static final Map<IVdmElement, Map<ILexLocation, INode>> elementNodeCache = new HashMap<IVdmElement, Map<ILexLocation, INode>>();

	private IVdmElement currentElement = null;

	private boolean indexing = false;

	/**
	 * Private constructor, special care is needed to the state of the class this no instanciation allowed outside this
	 * class
	 */
	private AstLocationSearcher()
	{
	}

	private void init()
	{
		seacher._visitedNodes.clear();// We cheat with undeclared exception, this breaks the state of the adaptor, and
										// we use
		// static so we need to clear the cache.
		seacher.bestHit = null;
		seacher.bestAlternativeHit = null;
		seacher.bestAlternativeLocation = null;
		seacher.currentElement = null;
		seacher.indexing = false;
	}

	/**
	 * Search method to find the closest node to a location specified by a test offset
	 * 
	 * @param nodes
	 *            The nodes to search within
	 * @param offSet
	 *            The offset to match a node to
	 * @return The node closest to the offset or null
	 */
	public static INode search(List<INode> nodes, int offSet,
			IVdmSourceUnit source)
	{
		synchronized (seacher)
		{
			if (DEBUG_PRINT)
			{
				System.out.println("Search start");
			}
			seacher.init();
			seacher.offSet = offSet;
			seacher.sourceFile = source.getSystemFile();
			try
			{
				for (INode node : nodes)
				{
					node.apply(seacher);
				}
			} catch (AnalysisException e)
			{
				// We found what we are looking for
			}

			return seacher.bestHit != null ? seacher.bestHit
					: seacher.bestAlternativeHit;
		}

	}

	/**
	 * Search method to find the closest node to a location specified by a test offset
	 * 
	 * @param nodes
	 *            The nodes to search within
	 * @param offSet
	 *            The offset to match a node to
	 * @param element
	 * @return The node closest to the offset or null
	 */
	public static INode searchCache(List<INode> nodes, int offSet,
			IVdmElement element)
	{
		synchronized (seacher)
		{
			if (DEBUG_PRINT)
			{
				System.out.println("Search start");
			}
			seacher.init();
			seacher.offSet = offSet;
			seacher.currentElement = element;
			try
			{
				if (elementNodeCache.get(element) == null
						|| elementNodeCache.get(element).isEmpty())
				{
					// elementNodeCache.put(element, new HashMap<ILexLocation, INode>());
					// seacher.indexing = true;
					// for (INode node : nodes)
					// {
					// node.apply(seacher);
					// }
					return null;
				} else
				{
					for (Entry<ILexLocation, INode> entry : elementNodeCache.get(element).entrySet())
					{
						seacher.check(entry.getValue(), entry.getKey());
					}
				}

			} catch (AnalysisException e)
			{
				// We found what we are looking for
			}

			return seacher.bestHit != null ? seacher.bestHit
					: seacher.bestAlternativeHit;
		}

	}

	public static void createIndex(List<INode> nodes, IVdmElement element)
			throws Throwable
	{
		seacher.init();
		seacher.currentElement = element;
		elementNodeCache.put(element, new HashMap<ILexLocation, INode>());
		seacher.indexing = true;
		for (INode node : nodes)
		{
			node.apply(seacher);
		}
	}

	@Override
	public void defaultInPDefinition(PDefinition node) throws AnalysisException
	{
		check(node, node.getLocation());
	}

	@Override
	public void defaultInPExp(PExp node) throws AnalysisException
	{
		check(node, node.getLocation());
	}

	@Override
	public void defaultInPStm(PStm node) throws AnalysisException
	{
		check(node, node.getLocation());
	}

	@Override
	public void caseAVariableExp(AVariableExp node) throws AnalysisException
	{
		check(node, node.getLocation());
	}

	@Override
	public void caseAFieldField(AFieldField node) throws AnalysisException
	{
		check(node, node.getTagname().getLocation());
	}

	@Override
	public void defaultInPPattern(PPattern node) throws AnalysisException
	{
		check(node, node.getLocation());
	}

	@Override
	public void caseAFunctionType(AFunctionType node)
	{
		// Skip
	}

	@Override
	public void caseARecordInvariantType(ARecordInvariantType node)
			throws AnalysisException
	{
		if (node.parent() instanceof ATypeDefinition)
		{
			super.caseARecordInvariantType(node);
		}
		// Skip
	}

	private void check(INode node, ILexLocation location)
			throws AnalysisException
	{
		if (DEBUG_PRINT)
		{
			System.out.println("Checking location span " + offSet + ": "
					+ location.getStartOffset() + " to "
					+ location.getEndOffset() + " line: "
					+ location.getStartLine() + ":" + location.getStartPos());
		}
		if (currentElement != null)
		{
			elementNodeCache.get(currentElement).put(location, node);
		}
		if (location.getStartOffset() - 1 <= this.offSet
				&& location.getEndOffset() - 1 >= this.offSet
				&& location.getFile().equals(sourceFile))
		{
			bestHit = node;
			if (!indexing)
			{
				throw new AnalysisException("Hit found stop search");
			}
		}

		// Store the last best match where best is closest with abs
		if (bestAlternativeLocation == null
				|| Math.abs(offSet - location.getStartOffset()) <= Math.abs(offSet
						- bestAlternativeLocation.getStartOffset())
				&& location.getFile().equals(sourceFile))
		{
			bestAlternativeLocation = location;
			bestAlternativeHit = node;
			if (DEBUG_PRINT)
			{
				System.out.println("Now best is: " + offSet + ": "
						+ location.getStartOffset() + " to "
						+ location.getEndOffset() + " line: "
						+ location.getStartLine() + ":"
						+ location.getStartPos());
			}
		} else if (bestAlternativeLocation == null
				|| offSet - bestAlternativeLocation.getStartOffset() > 0
				&& Math.abs(offSet - location.getStartOffset()) > Math.abs(offSet
						- bestAlternativeLocation.getStartOffset())
				&& location.getFile().equals(sourceFile))
		{
			if (DEBUG_PRINT)
			{
				System.out.println("Going back...");
			}
		} else
		{
			if (DEBUG_PRINT)
			{
				System.out.println("Rejected is: " + offSet + ": "
						+ location.getStartOffset() + " to "
						+ location.getEndOffset() + " line: "
						+ location.getStartLine() + ":"
						+ location.getStartPos());
			}
			if (!indexing)
			{
				throw new AnalysisException("Hit found stop search");
			}
		}
	}

	public static int[] getNodeOffset(INode node)
	{
		if (node instanceof PDefinition)
		{
			return getNodeOffset(((PDefinition) node).getLocation());
		} else if (node instanceof PExp)
		{
			return getNodeOffset(((PExp) node).getLocation());
		} else if (node instanceof PStm)
		{
			return getNodeOffset(((PStm) node).getLocation());
		} else if (node instanceof AFieldField)
		{
			return getNodeOffset(((AFieldField) node).getTagname().getLocation());
		} else if (node instanceof PImport)
		{
			return getNodeOffset(((PImport) node).getLocation());
		} else if (node instanceof PExport)
		{
			return getNodeOffset(((PExport) node).getLocation());
		} else if (node instanceof AFromModuleImports)
		{
			return getNodeOffset(((AFromModuleImports) node).getName().getLocation());
		} else if (node instanceof ImportsContainer)
		{
			return getNodeOffset(((ImportsContainer) node).getImports().getImports().getFirst());
		} else if (node instanceof AModuleImports)
		{
			return getNodeOffset(((AModuleImports) node).getName().getLocation());
		} else if (node instanceof AModuleModules)
		{
			return getNodeOffset(((AModuleModules) node).getName().getLocation());
		}
		return new int[] { -1, -1 };
	}

	public static int[] getNodeOffset(ILexLocation location)
	{
		return new int[] { location.getStartOffset() - 1,
				location.getEndOffset() - location.getStartOffset() };
	}
}
