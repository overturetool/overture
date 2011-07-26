package %generated.node%;

import java.util.*;

import org.overture.ast.node.NodeList;

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
public class %NodeList%<E extends %Node%> extends LinkedList<E> {
	%Node% parent;
	
	protected void setParent(%Node% n) {
		%Node% p = n.parent();
		if (p != null) {
			p.removeChild(n);
		}
		n.parent(parent);
	}
	
	public %NodeList%(%Node% parent) {
		super();
		this.parent = parent;
	}
	
	public %NodeList%(%Node% parent, Collection<? extends E> c) {
		this(parent);
		addAll(c);
	}
	
	public @Override boolean add(E o) {
		setParent(o);
		return super.add(o);
	}
	
	public @Override void addFirst(E o) {
		setParent(o);
		super.addFirst(o);
	}
	
	public @Override void addLast(E o) {
		setParent(o);
		super.addLast(o);
	}
	
	public @Override boolean remove(Object o) {
		if (super.remove(o)) {
			((%Node%)o).parent(null);
			return true;
		}
		return false;
	}
	
	public @Override E removeFirst() {
		E o = super.removeFirst();
		o.parent(null);
		return o;
	}
	
	public @Override E removeLast() {
		E o = super.removeLast();
		o.parent(null);
		return o;
	}
	
	public @Override void clear() {
		for (E o : this) {
			o.parent(null);
		}
		super.clear();
	}
	
	public @Override Object clone() {
		LinkedList<List<E>> clone = new LinkedList<List<E>>();
		for (List<E> list : clone)
		{
			NodeList<E> ll = new NodeList<E>(null);
			ll.addAll(list);
			clone.add((List<E>) ll.clone());
		}
		return clone;
	}
	
	public @Override E remove(int index) {
		E old = super.remove(index);
		old.parent(null);
		return old;
	}
	
	// We assume the the one-arg addAll method of LinkedList
	// calls the two-arg version
	
	public @Override boolean addAll(int index, Collection<? extends E> c) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		if (c == this) {
			return false;
		}
	
		// Adjust index if some of the nodes were already in the list
		// before the insertion position
		int i = 0;
		for (E elem : this) {
			if (i >= index) break;
			if (c.contains(elem)) index--;
			i++;
		}
	
		ArrayList<E> copy = new ArrayList<E>(c);
		for (E o : copy) {
			setParent(o);
		}
		return super.addAll(index, copy);
	}
	
	public @Override void add(int index, E o) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		// Adjust index if the node was already in the list
		// before the insertion position
		int i = 0;
		for (E elem : this) {
			if (i >= index) break;
			if (elem == o) index--;
			i++;
		}
	
		setParent(o);
		super.add(index, o);
	}
	
	public @Override E set(int index, E o) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+size());
		}
	
		// Adjust index if the node was already in the list
		// before the insertion position
		int i = 0;
		for (E elem : this) {
			if (i == index && elem == o) return o;
			if (i >= index) break;
			if (elem == o) index--;
			i++;
		}
	
		setParent(o);
		E old = super.set(index, o);
		old.parent(null);
		return old;
	}
	
    public @Override ListIterator<E> listIterator(int index) {
		return new NodeListIterator(super.listIterator(index));
    }

	private class NodeListIterator implements ListIterator<E> {
		ListIterator<E> iterator;
		E last_returned;
//		boolean previous;
		
		NodeListIterator(ListIterator<E> iterator) {
			this.iterator = iterator;
		}
		
		public boolean hasNext() {
			return iterator.hasNext();
		}
		
		public E next() {
//			previous = false;
			return last_returned = iterator.next();
		}
		
		public boolean hasPrevious() {
			return iterator.hasPrevious();
		}
		
		public E previous() {
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
		    last_returned.parent(null);
		}
		
		public void set(E o) {
			// This works but invalidates the iterator if the node was in
			// the list already.
			iterator.set(o);
			if (o != last_returned) {
				setParent(o);
				last_returned.parent(null);
				last_returned = o;
			}
		}
		
		public void add(E o) {
			// This works but invalidates the iterator if the node was in
			// the list already.
			iterator.add(o);
			setParent(o);
		}
	}
	}
