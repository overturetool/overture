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
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;

public class AstLocationSearcher2 extends AstLocationSearcherBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class TextReference
	{
		int offset;
		File source;

		public TextReference(File source, int offset)
		{
			this.source = source;
			this.offset = offset;
		}

		public boolean contains(int startOffset, int endOffset, File file)
		{
			return (source.equals(file) && offset >= startOffset && offset <= endOffset);
		}

		public boolean canMatch(File file)
		{
			return source.equals(file);
		}
	}

	public static class LocationFound extends AnalysisException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public AstLocation location;

		public LocationFound(AstLocation location)
		{
			this.location = location;
		}
	}

	public static class AstLocation
	{
		public INode node;
		public ILexLocation location;

		public AstLocation(INode node, ILexLocation location)
		{
			this.node = node;
			this.location = location;
		}

		/**
		 * Calculates the span of the location.
		 * 
		 * @return
		 */
		public int getSpan()
		{
			return location.getEndOffset() - location.getStartOffset();
		}

		/**
		 * Gets the smallest distance from this node to the offset, this might be either the start offset or the end
		 * offset
		 * 
		 * @param offset
		 * @return
		 */
		public int getDistance(int offset)
		{
			int startDistance = Math.abs(offset - location.getStartOffset());
			int endDistance = Math.abs(offset - location.getEndOffset());
			if (startDistance < endDistance)
			{
				return startDistance;
			}
			return endDistance;
		}
	}

	/**
	 * The text reference used to search for a coresponding node
	 */
	private TextReference reference;

	/**
	 * tracks the last "current" node visited, this have to be the owner of any location
	 */
	private INode current;

	/**
	 * closest node to text reference
	 */
	private AstLocation closest;

	
	/**
	 * Never called by analysis from super
	 */
	@Override
	public void caseILexLocation(ILexLocation node) throws AnalysisException
	{
		AstLocation location = new AstLocation(current, node);
		if (reference.contains(node.getStartOffset(), node.getEndOffset(), node.getFile()))// we need to do set some
																							// upper limit on the
																							// precision here. e.g. an
																							// operation may match but
																							// it has a body that may
																							// contain a better match
		{
			throw new LocationFound(location);
		} else if(reference.canMatch(node.getFile()))
		{
			closest = getClosest(reference, location);
		}
	}

	private AstLocation getClosest(TextReference reference, AstLocation choice)
	{
		if(closest==null)
		{
			return choice;
		}
		int closestDistance = closest.getDistance(reference.offset);
		int choiceDistance = choice.getDistance(reference.offset);
		if(choiceDistance<closestDistance)
		{
			return choice;
		}else if(choiceDistance == closestDistance)
		{
			if(choice.getSpan()<closest.getSpan())//consider offset here to prefer after or before nodes
			{
				return choice;
			}
		}
		return closest;
	}

	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		this.current = node;
		super.defaultInINode(node);
	}

	public INode getNode(TextReference reference, List<INode> ast)
	{
		this.current = null;
		this.reference=reference;
		try
		{
			for (INode iNode : ast)
			{
				iNode.apply(this);
			}
		} catch (LocationFound lf)
		{
			return lf.location.node;
		} catch (AnalysisException e)
		{
		}

		if (closest == null)
		{
			return null;
		}
		return closest.node;
	}

}
