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
public class NodeListList<E extends INode> extends LinkedList<List<E>> {
	INode parent;
	
	protected void setParentOfInnterList(List<? extends E> list, INode parent)
	{
		for (E e : list)
		{
			e.parent(parent);
		}
	}
	
	protected void setParent(List<? extends E> list) {
		for (E n : list)
		{
			
		INode p = n.parent();
		if (p != null) {
			p.removeChild(n);
		}
		n.parent(parent);
		}
	}
	
	public NodeListList(INode parent) {
		super();
		this.parent = parent;
	}
	
	public NodeListList(INode parent, Collection<List<E>> c) {
		this(parent);
		addAll(c);
	}
	
	
	
	public @Override boolean add(List<E> o) {
		setParent(o);
		return super.add(o);
	}
	
	public @Override void addFirst(List<E> o) {
		setParent(o);
		super.addFirst(o);
	}
	
	public @Override void addLast(List<E> o) {
		setParent(o);
		super.addLast(o);
	}
	
	public @Override boolean remove(Object o) {
		if (super.remove(o)) {
			((INode)o).parent(null);
			return true;
		}
		return false;
	}
	
	public @Override List<E> removeFirst() {
		List<E> o = super.removeFirst();
		setParentOfInnterList(	o,null);
		return o;
	}
	
	public @Override List<E> removeLast() {
		List<E> o = super.removeLast();
		setParentOfInnterList(	o,null);
		return o;
	}
	
	public @Override void clear() {
		for (List<? extends E> o : this) {
			setParentOfInnterList(	o,null);
		}
		super.clear();
	}
	
	@SuppressWarnings("unchecked")
	public @Override Object clone() {
		LinkedList<List<E>> clone = new LinkedList<List<E>>();
		for (List<E> list : this)
		{
			NodeList<E> ll = new NodeList<E>(null);
			for (E e : list) {
				ll.add((E) e.clone());
			}
			clone.add(ll);
		}
		return clone;
	}
	
	public @Override List<E> remove(int index) {
		List<E> old = super.remove(index);
		setParentOfInnterList(old,null);
		return old;
	}
	
	// We assume the the one-arg addAll method of LinkedList
	// calls the two-arg version
	public @Override boolean addAll(int index, Collection<? extends List<E>> c) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		if (c == this) {
			return false;
		}
	
		// Adjust index if some of the nodes were already in the list
		// before the insertion position
		int i = 0;
		for (List<? extends E> elem : this) {
			if (i >= index) break;
			if (c.contains(elem)) index--;
			i++;
		}
	
		ArrayList<List<E>> copy = new ArrayList<List<E>>(c);
		for (List<? extends E> o : copy) {
			setParent(o);
		}
		return super.addAll(index, copy);
	}
	
	public @Override void add(int index, List<E> o) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		// Adjust index if the node was already in the list
		// before the insertion position
		int i = 0;
		for (List<? extends E> elem : this) {
			if (i >= index) break;
			if (elem == o) index--;
			i++;
		}
	
		setParent(o);
		super.add(index, o);
	}
	
	public @Override List<E> set(int index, List<E> o) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		// Adjust index if the node was already in the list
		// before the insertion position
		int i = 0;
		for (List<? extends E> elem : this) {
			if (i == index && elem == o) return o;
			if (i >= index) break;
			if (elem == o) index--;
			i++;
		}
	
		setParent(o);
		List<E> old = super.set(index, o);
		setParentOfInnterList(old,null);
		return old;
	}
	
    public @Override ListIterator<List<E>> listIterator(int index) {
		return new NodeListIterator(super.listIterator(index));
    }

	private class NodeListIterator implements ListIterator<List<E>> {
		ListIterator<List<E>> iterator;
		List<E> last_returned;
//		boolean previous;
		
		NodeListIterator(ListIterator<List<E>> iterator) {
			this.iterator = iterator;
		}
		
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		public List<E> next() {
//			previous = false;
			return last_returned = iterator.next();
		}
		
		public boolean hasPrevious() {
			return iterator.hasPrevious();
		}
		
		public List<E> previous() {
//			previous = true;
			return last_returned = iterator.previous();
		}
		
		public int nextIndex() {
			return iterator.nextIndex();
		}
		
		public int previousIndex() {
			return iterator.previousIndex();
		}
		
		public void remove() {
		    iterator.remove();
		    setParentOfInnterList(  last_returned,null);
		}
		
		public void set(List<E> o) {
			// This works but invalidates the iterator if the node was in
			// the list already.
			iterator.set(o);
			if (o != last_returned) {
				setParent(o);
				  setParentOfInnterList(  last_returned,null);
				last_returned = o;
			}
		}
		
		public void add(List<E> o) {
			// This works but invalidates the iterator if the node was in
			// the list already.
			iterator.add(o);
			setParent(o);
		}
	}
	}
