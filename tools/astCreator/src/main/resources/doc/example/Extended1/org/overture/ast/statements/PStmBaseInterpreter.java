
package org.overture.ast.statements;


import java.util.Map;

import org.overture.ast.node.INode;
import org.overture.ast.node.NodeEnumInterpreter;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.NodeInterpreter;
import java.util.HashMap;
import org.overture.ast.expressions.EExpInterpreter;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public abstract class PStmBaseInterpreter extends NodeInterpreter implements PStmInterpreter
{
	private static final long serialVersionUID = 1L;




	/**
	 * Creates a new {@link PExpBaseInterpreter} node with no children.
	 */
	public PStmBaseInterpreter()
	{

	}


	/**
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
	@Override
	public abstract PStmInterpreter clone(Map<INode,INode> oldToNewMap);

	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link INodeInterpreter} node.
	 * @return the {@link NodeEnum} for this node
	 */
	@Override
	public NodeEnumInterpreter kindNodeInterpreter()
	{
		return NodeEnumInterpreter.STM;
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
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof PStmBaseInterpreter)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link PExpBaseInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link PExpBaseInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link PExpBaseInterpreter} node
	 */
	public void removeChild(INodeInterpreter child)
	{
		throw new RuntimeException("Not a child.");
	}


	/**
	 * Returns a deep clone of this {@link PExpBaseInterpreter} node.
	 * @return a deep clone of this {@link PExpBaseInterpreter} node
	 */
	@Override
	public abstract PStmInterpreter clone();


}
