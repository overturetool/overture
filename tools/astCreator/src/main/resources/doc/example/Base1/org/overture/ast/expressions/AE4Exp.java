
package org.overture.ast.expressions;


import org.overture.ast.analysis.intf.IAnalysis;
import java.util.Map;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.AE4Exp;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.node.INode;
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
public class AE4Exp extends PExpBase
{
	private static final long serialVersionUID = 1L;

	/**
	* Graph field, parent will not be removed when added and parent 
	*  of this field may not be this node. Also excluded for visitor.
	*/
	protected AE1Exp _field2;

	/**
	* Creates a new {@code AE4Exp} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param field2_ the {@link AE1Exp} <b>graph</a> node for the {@code field2} child of this {@link AE4Exp} node.
	*  <i>The parent of this {@code field2 } will not be changed by adding it to this node.</i>
	*/
	public AE4Exp(AE1Exp field2_)
	{
		super();
		this.setField2(field2_);

	}


	/**
	* Creates a new {@code AE4Exp TAG=e4} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	*/
	public AE4Exp()
	{
		super();

	}


//	/**
//	 * Creates a new {@link AE4Exp} node with no children.
//	 */
//	public AE4Exp()
//	{
//
//	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	@Override
	public EExp kindPExp()
	{
		return EExp.E4;
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
		fields.put("_field2",this._field2);
		return fields;
	}


	/**
	 * Creates a deep clone of this {@link AE4Exp} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE4Exp} node
	 */
	public AE4Exp clone(Map<INode,INode> oldToNewMap)
	{
		AE4Exp node = new AE4Exp(
			_field2
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AE4Exp)		{
			 return toString().equals(o.toString());
		}
		return false;
	}



	public String toString()
	{
		return (_field2!=null?_field2.toString():this.getClass().getSimpleName());
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AE4Exp} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE4Exp} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE4Exp} node
	 */
	public void removeChild(INode child)
	{
		if (this._field2 == child) {
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	 * Returns a deep clone of this {@link AE4Exp} node.
	 * @return a deep clone of this {@link AE4Exp} node
	 */
	public AE4Exp clone()
	{
		return new AE4Exp(
			_field2
		);
	}


	/**
	 * Sets the {@code _field2} child of this {@link AE4Exp} node.
	 * @param value the new {@code _field2} child of this {@link AE4Exp} node
	*/
	public void setField2(AE1Exp value)
	{
		if( value != null && value.parent() == null) {
			value.parent(this);
		}
		this._field2 = value;

	}


	/**
	 * @return the {@link AE1Exp} node which is the {@code _field2} child of this {@link AE4Exp} node
	*/
	public AE1Exp getField2()
	{
		return this._field2;
	}


	/**
	* Calls the {@link IAnalysis#caseAE4Exp(AE4Exp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AE4Exp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseAE4Exp(this);
	}


	/**
	* Calls the {@link IAnswer#caseAE4Exp(AE4Exp)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AE4Exp} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseAE4Exp(this);
	}


	/**
	* Calls the {@link IQuestion#caseAE4Exp(AE4Exp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AE4Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseAE4Exp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAE4Exp(AE4Exp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AE4Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseAE4Exp(this, question);
	}



}
