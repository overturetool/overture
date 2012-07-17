
package org.overture.ast.expressions;


import org.overture.ast.node.NodeEnum;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.Node;
import java.util.Map;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.EExp;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public abstract class PExpBase extends Node implements PExp
{
	private static final long serialVersionUID = 1L;




	/**
	 * Creates a new {@link PExpBase} node with no children.
	 */
	public PExpBase()
	{

	}



	public String toString()
	{
		return super.toString();

	}


	/**
	 * Creates a map of all field names and their value
	 * @param includeInheritedFields if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	@Override
	public Map<String,Object> getChildren(Boolean includeInheritedFields)
	{
		Map<String,Object> fields = new HashMap<String,Object>();
		if(includeInheritedFields)
		{
			fields.putAll(super.getChildren(includeInheritedFields));
		}
		return fields;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link PExpBase} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link PExpBase} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link PExpBase} node
	 */
	public void removeChild(INode child)
	{
		throw new RuntimeException("Not a child.");
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof PExpBase)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	public abstract EExp kindPExp();

	/**
	 * Creates a deep clone of this {@link PExpBase} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link PExpBase} node
	 */
	@Override
	public abstract PExp clone(Map<INode,INode> oldToNewMap);

	/**
	 * Returns a deep clone of this {@link PExpBase} node.
	 * @return a deep clone of this {@link PExpBase} node
	 */
	@Override
	public abstract PExp clone();

	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link INode} node.
	 * @return the {@link NodeEnum} for this node
	 */
	@Override
	public NodeEnum kindNode()
	{
		return NodeEnum.EXP;
	}



}
