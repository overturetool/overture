
package org.overture.ast.expressions;


import org.overture.ast.node.NodeEnum;
import org.overture.ast.expressions.PExp;
import java.util.Map;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.EExp;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public interface PExp extends INode
{
	public String toString();
	/**
	 * Creates a map of all field names and their value
	 * @param includeInheritedFields if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	public Map<String,Object> getChildren(Boolean includeInheritedFields);
	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link PExpBase} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link PExpBase} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link PExpBase} node
	 */
	public void removeChild(INode child);
	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	public boolean equals(Object o);
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
	public abstract PExp clone(Map<INode,INode> oldToNewMap);
	/**
	 * Returns a deep clone of this {@link PExpBase} node.
	 * @return a deep clone of this {@link PExpBase} node
	 */
	public abstract PExp clone();
	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link INode} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public NodeEnum kindNode();

}
