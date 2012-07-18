/*******************************************************************************
* Copyright (c) 2009, 2011 Overture Team and others.
*
* Overture is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Overture is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Overture.  If not, see <http://www.gnu.org/licenses/>.
*
* The Overture Tool web-site: http://overturetool.org/
*******************************************************************************/

package org.overture.ast.node;

import java.util.*;

/** A list of AST nodes where all operations preserve the
 *  single-parent property of the AST.<p>
 *  A node list is always a child list of some parent node.<p>
 *  When a node is added to the list (through the collection constructor,
 *  the <code>add</code>, <code>addFirst</code>, <code>addLast</code>,
 *  <code>addAll</code> or <code>set</code> methods of the list or
 *  the <code>add</code> or <code>set</code> methods of the iterator),
 *  it is removed from its original parent (if it has one) and its parent
 *  is set to the node containing the node list.<p>
 *  When a node is removed from the list (through the <code>remove</code>,
 *  <code>removeFirst</code>, <code>removeLast</code>, <code>clear</code> or
 *  <code>set</code> methods of the list or the <code>remove</code> or
 *  <code>set</code> methods of the iterator), its parent is set to
 *  <code>null</code>.<p>
 *  Beware that if the <code>add</code> or <code>set</code> method of the
 *  iterator is called with a node which is already in the list (except for a
 *  <code>set</code> call replacing a node by itself), the iterator
 *  will be invalidated, so any subsequent iterator operation will throw a
 *  <code>ConcurrentModificationException</code>.<p>
 *
 */
@SuppressWarnings("serial")
public class GraphNodeList<E extends INode> extends NodeList<E> {
	
	@Override
	protected void setParent(INode n) {
		//Don't change the structure for Graph fields
	}
	
	public GraphNodeList(INode parent) {
		super(null);
	}
	
	public GraphNodeList(INode parent, Collection<? extends E> c) {
		this(null);
		addAll(c);
	}
	
	
	
}
