/*
 * #%~
 * Combinatorial Testing Runtime
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
package org.overture.ct.ctruntime.utils;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterator implements Iterator<Node>, Iterable<Node>
{
	private final NodeList list;
	private int index = 0;

	public NodeIterator(NodeList list)
	{
		this.list = list;
	}

	@Override
	public boolean hasNext()
	{
		return index < list.getLength();
	}

	@Override
	public Node next()
	{
		return list.item(index++);
	}

	@Override
	public void remove()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Iterator<Node> iterator()
	{
		return this;
	}

}
