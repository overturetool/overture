//COPYRIGHT
package %generated.node%;


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
public class %GraphNodeListList%<E extends %INode%> extends %NodeListList%<E> {
		
	@Override
	protected void setParentOfInnterList(List<? extends E> list, %INode% parent)
	{
		//Don't change the structure for Graph fields
	}
	
	protected void setParent(List<? extends E> list) {
		//Don't change the structure for Graph fields unless the parent is null
		for (E n : list)
		{
			%INode% p = n.parent();
			if (p == null) {
				n.parent(parent);
			}
			
		}
	}
	
	public %GraphNodeListList%(%INode% parent) {
		super(null);
	}
	
	public %GraphNodeListList%(%INode% parent, Collection<List<E>> c) {
		this(parent);
		addAll(c);
	}
	}
