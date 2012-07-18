
package org.overture.ast.expressions;


import org.overture.ast.analysis.intf.IAnalysis;
import java.util.Map;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
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
public class AE3Exp extends PExpBase
{
	private static final long serialVersionUID = 1L;

	protected AE1Exp _field1;


	/**
	* Creates a new {@code AE3Exp} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param field1_ the {@link AE1Exp} node for the {@code field1} child of this {@link AE3Exp} node
	*/
	public AE3Exp(AE1Exp field1_)
	{
		super();
		this.setField1(field1_);

	}


	/**
	 * Creates a new {@link AE3Exp} node with no children.
	 */
	public AE3Exp()
	{

	}


	/**
	 * Returns a deep clone of this {@link AE3Exp} node.
	 * @return a deep clone of this {@link AE3Exp} node
	 */
	public AE3Exp clone()
	{
		return new AE3Exp(
			cloneNode(_field1)
		);
	}



	public String toString()
	{
		return (_field1!=null?_field1.toString():this.getClass().getSimpleName());
	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	@Override
	public EExp kindPExp()
	{
		return EExp.E3;
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
		fields.put("_field1",this._field1);
		return fields;
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AE3Exp)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AE3Exp} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE3Exp} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE3Exp} node
	 */
	public void removeChild(INode child)
	{
		if (this._field1 == child) {
			this._field1 = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	 * Creates a deep clone of this {@link AE3Exp} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE3Exp} node
	 */
	public AE3Exp clone(Map<INode,INode> oldToNewMap)
	{
		AE3Exp node = new AE3Exp(
			cloneNode(_field1, oldToNewMap)
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Sets the {@code _field1} child of this {@link AE3Exp} node.
	 * @param value the new {@code _field1} child of this {@link AE3Exp} node
	*/
	public void setField1(AE1Exp value)
	{
		if (this._field1 != null) {
			this._field1.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._field1 = value;

	}


	/**
	 * @return the {@link AE1Exp} node which is the {@code _field1} child of this {@link AE3Exp} node
	*/
	public AE1Exp getField1()
	{
		return this._field1;
	}


	/**
	* Calls the {@link IAnalysis#caseAE3Exp(AE3Exp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AE3Exp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseAE3Exp(this);
	}


	/**
	* Calls the {@link IAnswer#caseAE3Exp(AE3Exp)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AE3Exp} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseAE3Exp(this);
	}


	/**
	* Calls the {@link IQuestion#caseAE3Exp(AE3Exp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AE3Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseAE3Exp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAE3Exp(AE3Exp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AE3Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseAE3Exp(this, question);
	}



}
