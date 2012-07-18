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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;

public abstract class NodeInterpreter implements INodeInterpreter, Cloneable, Serializable, /*experimental compare based on toString*/Comparable<INodeInterpreter>
{
	private static final long serialVersionUID = 1L;
	
	private INodeInterpreter parent;
	
	public @Override abstract Object clone();
	public abstract INodeInterpreter clone(Map<INode,INode> oldToNewMap);
	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public abstract NodeEnumInterpreter kindNodeInterpreter();
	
	/**
	 * Returns the parent node of this node.
	 * @return the parent node of this node
	 */
	public INodeInterpreter parent() {
		return this.parent;
	}

	/**
	 * Sets the parent node of this node.
	 * @param parent the new parent node of this node
	 */
	public void parent(INodeInterpreter parent) {
		this.parent = parent;
	}
	
	/**
	 * Removes the {@link NodeInterpreter} {@code child} as a child of this node.
	 * @param child the child node to be removed from this node
	 * @throws RuntimeException if {@code child} is not a child of this node
	 */
	public abstract void removeChild(INodeInterpreter child);
	
//	/**
//	 * Replaces the {@link NodeInterpreter} {@code oldChild} child node of this node
//	 * with the {@link NodeInterpreter} {@code newChild}.
//	 * @param oldChild the child node to be replaced
//	 * @param newChild the new child node of this node
//	 * @throws RuntimeException if {@code oldChild} is not a child of this node
//	 */
//	abstract void replaceChild(NodeInterpreter oldChild, Node newChild);
//
//	/**
//	 * Replaces this node by {@code node} in the AST. If this node has no parent
//	 * node, this results in a {@link NullPointerException}.
//	 * The replacing {@code node} is removed from its previous parent.
//	 * @param node the node replacing this node in the AST
//	 */
//	public void replaceBy(NodeInterpreter node) {
//		this.parent.replaceChild(this, node);
//	}
	
	/**
	 * Returns a deep clone of {@code node} or {@code null} if {@code node} is {@code null}.
	 * @param node the node which is cloned
	 * @return a deep clone of {@code node}
	 */
	@SuppressWarnings("unchecked")
	protected <T extends INodeInterpreter> T cloneNode(T node) {
		if(node != null) {
			return (T) node.clone();
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	protected <T extends ExternalNodeInterpreter> T cloneNode(T node) {
		if(node != null) {
			return (T) node.clone();
		}
		return null;
	}

	/**
	 * Returns a deep clone of {@code node} or {@code null} if {@code node} is {@code null}.
	 * The old node-new node relation is put into {@code oldToNewMap}.
	 * @param node the node which is cloned
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of {@code node}
	 */
	@SuppressWarnings("unchecked")
	protected <T extends INodeInterpreter> T cloneNode(T node, java.util.Map<INode,INode> oldToNewMap) {
		if(node != null) {
			T clone = (T) node.clone(oldToNewMap);
			oldToNewMap.put(node,clone);
			return clone;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends ExternalNodeInterpreter> T cloneNode(T node, java.util.Map<INode,INode> oldToNewMap) {
		if(node != null) {
			T clone = (T) node.clone();//oldToNewMap);
//			oldToNewMap.put(node,clone);
			return clone;
		}
		return null;
	}

	/**
	 * Returns a deep clone of {@code list}.
	 * @param list the list which is cloned
	 * @return a deep clone of {@code list}
	 */
	@SuppressWarnings("unchecked")
	protected <T extends INodeInterpreter> List<T> cloneList(List<T> list) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			clone.add((T) n.clone());
		}
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends ExternalNodeInterpreter> List<T> cloneListExternal(List<T> list) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			clone.add((T) n.clone());
		}
		return clone;
	}
	
	protected <T extends INodeInterpreter> Collection<? extends List<T>> cloneListList(List<? extends List<T>> list) {
		LinkedList<List<T>> clone = new LinkedList< List<T>>();
		for(List<T> n : list) {
			clone.add( cloneList(n));
		}
		return clone;
	}

	/**
	 * Returns a deep clone of {@code list}.
	 * The old node-new node relations are put into {@code oldToNewMap}.
	 * @param list the list which is cloned
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of {@code list}
	 */
	@SuppressWarnings("unchecked")
	protected <T extends INodeInterpreter> List<T> cloneList(List<T> list, java.util.Map<INode,INode> oldToNewMap) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			T cloneNode = (T) n.clone(oldToNewMap);
			oldToNewMap.put(n, cloneNode);
			clone.add(cloneNode);
		}
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends ExternalNodeInterpreter> List<T> cloneListExternal(List<T> list, java.util.Map<INode,INode> oldToNewMap) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			T cloneNode = (T) n.clone();//oldToNewMap);
//			oldToNewMap.put(n, cloneNode);
			clone.add(cloneNode);
		}
		return clone;
	}
	
	protected <T extends INodeInterpreter> Collection<? extends List<T>> cloneListList(List<? extends List<T>> list, java.util.Map<INode,INode> oldToNewMap) {
		LinkedList<List<T>> clone = new LinkedList< List<T>>();
		for(List<T> n : list) {
			clone.add( cloneList(n,oldToNewMap));
		}
		return clone;
	}
	
	/**
	 * Returns the nearest ancestor of this node (including itself)
	 * which is a subclass of {@code classType}.
	 * @param classType the superclass used
	 * @return the nearest ancestor of this node
	 */
	public <T extends INode> T getAncestor(Class<T> classType) {
		INode n = this;
		while (!classType.isInstance(n)) {
			n = n.parent();
			if (n == null) return null;
		}
		return classType.cast(n);
	}
	
	/**
	 * CompareTo based on ToString
	 */
	public int compareTo(INodeInterpreter o) {
		return toString().compareTo(o.toString());
	}
	
	/**
	 * Applies this node to the {@link IAnalysis} visitor {@code analysis}.
	 * @param analysis the {@link IAnalysis} to which this node is applied
	 */
	public abstract void apply(IAnalysisInterpreter analysis);
	
	/**
	 * Returns the answer for {@code caller} by applying this node to the
	 * {@link IAnswer} visitor.
	 * @param caller the {@link IAnswer} to which this node is applied
	 * @return the answer as returned from {@code caller}
	 */
	public abstract <A> A apply(IAnswerInterpreter<A> caller);
	
	/**
	 * Applies this node to the {@link IQuestion} visitor {@code caller}.
	 * @param caller the {@link IQuestion} to which this node is applied
	 * @param question the question provided to {@code caller}
	 */
	public abstract <Q> void apply(IQuestionInterpreter<Q> caller, Q question);

	/**
	 * Returns the answer for {@code answer} by applying this node with the
	 * {@code question} to the {@link IQuestionAnswer} visitor.
	 * @param caller the {@link IQuestionAnswer} to which this node is applied
	 * @param question the question provided to {@code answer}
	 * @return the answer as returned from {@code answer}
	 */
	public abstract <Q,A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question);
		
	public Map<String,Object> getChildren(Boolean includeInheritedFields)
	{
		return new HashMap<String,Object>();
	}
}
