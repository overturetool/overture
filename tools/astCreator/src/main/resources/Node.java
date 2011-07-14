package %generated.node%;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.ast.node.Node;

import %org.overture.ast.analysis%.%IAnalysis%;
import %org.overture.ast.analysis%.%IAnswer%;
import %org.overture.ast.analysis%.%IQuestion%;
import %org.overture.ast.analysis%.%IQuestionAnswer%;

public abstract class %Node% implements Cloneable
{
	private %Node% parent;
	
	public @Override abstract Object clone();
	public abstract %Node% clone(Map<%Node%,%Node%> oldToNewMap);
	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public abstract %NodeEnum% kindNode();
	
	/**
	 * Returns the parent node of this node.
	 * @return the parent node of this node
	 */
	public %Node% parent() {
		return this.parent;
	}

	/**
	 * Sets the parent node of this node.
	 * @param parent the new parent node of this node
	 */
	public void parent(@SuppressWarnings("hiding") %Node% parent) {
		this.parent = parent;
	}
	
	/**
	 * Removes the {@link %Node%} {@code child} as a child of this node.
	 * @param child the child node to be removed from this node
	 * @throws RuntimeException if {@code child} is not a child of this node
	 */
	public abstract void removeChild(%Node% child);
	
//	/**
//	 * Replaces the {@link %Node%} {@code oldChild} child node of this node
//	 * with the {@link %Node%} {@code newChild}.
//	 * @param oldChild the child node to be replaced
//	 * @param newChild the new child node of this node
//	 * @throws RuntimeException if {@code oldChild} is not a child of this node
//	 */
//	abstract void replaceChild(%Node% oldChild, Node newChild);
//
//	/**
//	 * Replaces this node by {@code node} in the AST. If this node has no parent
//	 * node, this results in a {@link NullPointerException}.
//	 * The replacing {@code node} is removed from its previous parent.
//	 * @param node the node replacing this node in the AST
//	 */
//	public void replaceBy(%Node% node) {
//		this.parent.replaceChild(this, node);
//	}
	
	/**
	 * Returns a deep clone of {@code node} or {@code null} if {@code node} is {@code null}.
	 * @param node the node which is cloned
	 * @return a deep clone of {@code node}
	 */
	@SuppressWarnings("unchecked")
	protected <T extends %Node%> T cloneNode(T node) {
		if(node != null) {
			return (T) node.clone();
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	protected <T extends %ExternalNode%> T cloneNode(T node) {
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
	protected <T extends %Node%> T cloneNode(T node, java.util.Map<%Node%,%Node%> oldToNewMap) {
		if(node != null) {
			T clone = (T) node.clone(oldToNewMap);
			oldToNewMap.put(node,clone);
			return clone;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends %ExternalNode%> T cloneNode(T node, java.util.Map<%Node%,%Node%> oldToNewMap) {
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
	protected <T extends %Node%> List<T> cloneList(List<T> list) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			clone.add((T) n.clone());
		}
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends %ExternalNode%> List<T> cloneListExternal(List<T> list) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			clone.add((T) n.clone());
		}
		return clone;
	}
	
	protected <T extends %Node%> Collection<? extends List<T>> cloneListList(List<? extends List<T>> list) {
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
	protected <T extends %Node%> List<T> cloneList(List<T> list, java.util.Map<%Node%,%Node%> oldToNewMap) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			T cloneNode = (T) n.clone(oldToNewMap);
			oldToNewMap.put(n, cloneNode);
			clone.add(cloneNode);
		}
		return clone;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends %ExternalNode%> List<T> cloneListExternal(List<T> list, java.util.Map<%Node%,%Node%> oldToNewMap) {
		List<T> clone = new LinkedList<T>();
		for(T n : list) {
			T cloneNode = (T) n.clone();//oldToNewMap);
//			oldToNewMap.put(n, cloneNode);
			clone.add(cloneNode);
		}
		return clone;
	}
	
	protected <T extends %Node%> Collection<? extends List<T>> cloneListList(List<? extends List<T>> list, java.util.Map<Node,Node> oldToNewMap) {
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
	public <T extends %Node%> T getAncestor(Class<T> classType) {
		%Node% n = this;
		while (!classType.isInstance(n)) {
			n = n.parent();
			if (n == null) return null;
		}
		return classType.cast(n);
	}
	
	/**
	 * Applies this node to the {@link Analysis} visitor {@code analysis}.
	 * @param analysis the {@link Analysis} to which this node is applied
	 */
	public abstract void apply(%IAnalysis% analysis);
	
	/**
	 * Returns the answer for {@code caller} by applying this node to the
	 * {@link Answer} visitor.
	 * @param caller the {@link Answer} to which this node is applied
	 * @return the answer as returned from {@code caller}
	 */
	public abstract <A> A apply(%IAnswer<A>% caller);
	
	/**
	 * Applies this node to the {@link Question} visitor {@code caller}.
	 * @param caller the {@link Question} to which this node is applied
	 * @param question the question provided to {@code caller}
	 */
	public abstract <Q> void apply(%IQuestion<Q>% caller, Q question);

	/**
	 * Returns the answer for {@code answer} by applying this node with the
	 * {@code question} to the {@link QuestionAnswer} visitor.
	 * @param caller the {@link QuestionAnswer} to which this node is applied
	 * @param question the question provided to {@code answer}
	 * @return the answer as returned from {@code answer}
	 */
	public abstract <Q,A> A apply(%IQuestionAnswer<Q,A>% caller, Q question);
		
//	//////////////////////////////////// Try out //////////////////////////////////
//	/**
//	 * Ignored by clone
//	 */
//	final Map<String, Map<String, Object>> customFields = new Hashtable<String, Map<String, Object>>();
//
//	public void setCustomField(String pluginId, String field, Object value)
//	{
//		if (customFields.containsKey(pluginId))
//		{
//			Map<String, Object> fields = customFields.get(pluginId);
//			if (fields.containsKey(field))
//			{
//				fields.remove(field);
//			}
//			fields.put(field, value);
//		} else
//		{
//			Map<String, Object> fields = new Hashtable<String, Object>();
//			fields.put(field, value);
//			customFields.put(pluginId, fields);
//		}
//	}

//	@SuppressWarnings("unchecked")
//	public <T> T getCustomField(String pluginId, String field, T defaultValue)
//	{
//		if (customFields.containsKey(pluginId))
//		{
//			Map<String, Object> fields = customFields.get(pluginId);
//			if (fields.containsKey(field))
//			{
//				Object o = fields.get(field);
//				if(defaultValue.getClass().isInstance(o))
//				{
//					return (T) o;
//				}
//				
//			}
//		}
//		return defaultValue;
//	}
}
