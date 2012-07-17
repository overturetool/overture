
package org.overture.ast.expressions;


import org.overture.ast.expressions.PExp;
import java.util.Map;

import org.overture.ast.node.INode;
import org.overture.ast.node.NodeEnumInterpreter;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.expressions.PExpInterpreter;
import org.overture.ast.expressions.EExpInterpreter;


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
public interface PExpInterpreter extends PExp, INodeInterpreter
{	/**
	 * Returns the {@link EExpInterpreter} corresponding to the
	 * type of this {@link EExpInterpreter} node.
	 * @return the {@link EExpInterpreter} for this node
	 */
	public abstract EExpInterpreter kindPExpInterpreter();
	/**
	 * Creates a deep clone of this {@link PExpBaseInterpreter} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link PExpBaseInterpreter} node
	 */
	public abstract PExpInterpreter clone(Map<INode,INode> oldToNewMap);
	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link INodeInterpreter} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public NodeEnumInterpreter kindNodeInterpreter();

	public String toString();
	/**
	 * Creates a map of all field names and their value
	 * @param includeInheritedFields if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	public Map<String,Object> getChildren(Boolean includeInheritedFields);
	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	public boolean equals(Object o);
	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link PExpBaseInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link PExpBaseInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link PExpBaseInterpreter} node
	 */
	public void removeChild(INodeInterpreter child);
	/**
	 * Returns a deep clone of this {@link PExpBaseInterpreter} node.
	 * @return a deep clone of this {@link PExpBaseInterpreter} node
	 */
	public abstract PExpInterpreter clone();

}
