
package org.overture.ast.expressions;


import org.overture.ast.analysis.intf.IAnalysis;
import java.util.Map;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.PExpBase;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.expressions.EExp;
import org.overture.ast.analysis.intf.IQuestion;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AE1Exp extends PExpBase
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link AE1Exp} node with no children.
	 */
	public AE1Exp()
	{

	}




	public String toString()
	{
		return super.toString();
	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	@Override
	public EExp kindPExp()
	{
		return EExp.E1;
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AE1Exp)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Returns a deep clone of this {@link AE1Exp} node.
	 * @return a deep clone of this {@link AE1Exp} node
	 */
	public AE1Exp clone()
	{
		return new AE1Exp(
		);
	}


	/**
	 * Creates a deep clone of this {@link AE1Exp} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE1Exp} node
	 */
	public AE1Exp clone(Map<INode,INode> oldToNewMap)
	{
		AE1Exp node = new AE1Exp(
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AE1Exp} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE1Exp} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE1Exp} node
	 */
	public void removeChild(INode child)
	{
		throw new RuntimeException("Not a child.");
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
	* Calls the {@link IAnalysis#caseAE1Exp(AE1Exp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AE1Exp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseAE1Exp(this);
	}


	/**
	* Calls the {@link IAnswer#caseAE1Exp(AE1Exp)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AE1Exp} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseAE1Exp(this);
	}


	/**
	* Calls the {@link IQuestion#caseAE1Exp(AE1Exp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AE1Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseAE1Exp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAE1Exp(AE1Exp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AE1Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseAE1Exp(this, question);
	}



}
