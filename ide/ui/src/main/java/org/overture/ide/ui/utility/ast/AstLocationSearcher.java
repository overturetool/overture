package org.overture.ide.ui.utility.ast;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ide.core.IVdmElement;
import org.overturetool.vdmj.lex.LexLocation;

/**
 * Class used by an editor to search the editor text for source code node locations. Used to find nodes in the source
 * code to sync with outline
 * 
 * @author kela
 */
public final class AstLocationSearcher extends DepthFirstAnalysisAdaptor
{
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;

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
	private LexLocation bestAlternativeLocation;
	/**
	 * The offset used when searching for nodes within this location of the source code
	 */
	private int offSet;

	private static final AstLocationSearcher seacher = new AstLocationSearcher();

	private static final Map<IVdmElement, Map<LexLocation, INode>> elementNodeCache = new HashMap<IVdmElement, Map<LexLocation, INode>>();

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
		seacher._queue.clear();// We cheat with undeclared exception, this breaks the state of the adaptor, and we use
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
	public static INode search(List<INode> nodes, int offSet)
	{
		synchronized (seacher)
		{
			if (DEBUG_PRINT)
			{
				System.out.println("Search start");
			}
			seacher.init();
			seacher.offSet = offSet;
			try
			{
				for (INode node : nodes)
				{
					node.apply(seacher);
				}
			} catch (UndeclaredThrowableException e)
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
					// elementNodeCache.put(element, new HashMap<LexLocation, INode>());
					// seacher.indexing = true;
					// for (INode node : nodes)
					// {
					// node.apply(seacher);
					// }
					return null;
				} else
				{
					for (Entry<LexLocation, INode> entry : elementNodeCache.get(element).entrySet())
					{
						seacher.check(entry.getValue(), entry.getKey());
					}
				}

			} catch (UndeclaredThrowableException e)
			{
				// We found what we are looking for
			}

			return seacher.bestHit != null ? seacher.bestHit
					: seacher.bestAlternativeHit;
		}

	}

	public static void createIndex(List<INode> nodes, IVdmElement element)
	{
		seacher.init();
		seacher.currentElement = element;
		elementNodeCache.put(element, new HashMap<LexLocation, INode>());
		seacher.indexing = true;
		for (INode node : nodes)
		{
			node.apply(seacher);
		}
	}

	@Override
	public void defaultInPDefinition(PDefinition node)
	{
		check(node, node.getLocation());
	}

	@Override
	public void defaultInPExp(PExp node)
	{
		check(node, node.getLocation());
	}

	@Override
	public void defaultInPStm(PStm node)
	{
		check(node, node.getLocation());
	}

	@Override
	public void caseAVariableExp(AVariableExp node)
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
	{
		// Skip
	}

	private void check(INode node, LexLocation location)
	{
		if (DEBUG_PRINT)
		{
			System.out.println("Checking location span " + offSet + ": "
					+ location.startOffset + " to " + location.endOffset
					+ " line: " + location.startLine + ":" + location.startPos);
		}
		if (currentElement != null)
		{
			elementNodeCache.get(currentElement).put(location, node);
		}
		if (location.startOffset - 1 <= this.offSet
				&& location.endOffset - 1 >= this.offSet)
		{
			bestHit = node;
			if (!indexing)
			{
				throw new UndeclaredThrowableException(null, "Hit found stop search");
			}
		}

		// Store the last best match where best is closest with abs
		if (bestAlternativeLocation == null
				|| Math.abs(offSet - location.startOffset) <= Math.abs(offSet
						- bestAlternativeLocation.startOffset))
		{
			bestAlternativeLocation = location;
			bestAlternativeHit = node;
			if (DEBUG_PRINT)
			{
				System.out.println("Now best is: " + offSet + ": "
						+ location.startOffset + " to " + location.endOffset
						+ " line: " + location.startLine + ":"
						+ location.startPos);
			}
		} else if (bestAlternativeLocation == null
				|| (offSet - bestAlternativeLocation.startOffset > 0)
				&& Math.abs(offSet - location.startOffset) > Math.abs(offSet
						- bestAlternativeLocation.startOffset))
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
						+ location.startOffset + " to " + location.endOffset
						+ " line: " + location.startLine + ":"
						+ location.startPos);
			}
			if (!indexing)
			{
				throw new UndeclaredThrowableException(null, "Hit found stop search");
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
		}
		return new int[] { -1, -1 };
	}

	public static int[] getNodeOffset(LexLocation location)
	{
		return new int[] { location.startOffset - 1,
				location.endOffset - location.startOffset };
	}
}
