
package org.overture.ast.expressions;


import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;
import java.util.Map;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import org.overture.ast.expressions.EExp;
import org.overture.ast.expressions.EExpInterpreter;
import java.util.HashMap;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.node.INode;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.NodeEnumInterpreter;
import org.overture.ast.expressions.PExpInterpreter;
import org.overture.ast.expressions.AE1ExpInterpreter;
import org.overture.ast.analysis.intf.IQuestion;

/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AE3ExpInterpreter extends AE3Exp implements PExpInterpreter
{
	private static final long serialVersionUID = 1L;

//	private INodeInterpreter _parent;
	protected AE2ExpInterpreter _field2;

	/**
	* Creates a new {@code AE3ExpInterpreter} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param field1_ the {@link AE1ExpInterpreter} node for the {@code field1} child of this {@link AE3ExpInterpreter} node
	* @param parent_ the {@link INodeInterpreter} node for the {@code parent} child of this {@link AE3ExpInterpreter} node
	*/
	public AE3ExpInterpreter(AE1ExpInterpreter field1_, AE2ExpInterpreter field2_)
	{
		super(null);
		this.setField1(field1_);
		this.setField2(field2_);

	}



	/**
	 * Creates a new {@link AE3ExpInterpreter} node with no children.
	 */
	public AE3ExpInterpreter()
	{

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
	 * Creates a deep clone of this {@link AE3ExpInterpreter} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE3ExpInterpreter} node
	 */
	public AE3ExpInterpreter clone(Map<INode,INode> oldToNewMap)
	{
		AE3ExpInterpreter node = new AE3ExpInterpreter(
			cloneNode((AE1ExpInterpreter)_field1, oldToNewMap),
			cloneNode((AE2ExpInterpreter)_field2, oldToNewMap)
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
		if (o != null && o instanceof AE3ExpInterpreter)		{
			 return toString().equals(o.toString());
		}
		return false;
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



	public String toString()
	{
		return (_field1!=null?_field1.toString():this.getClass().getSimpleName())+ (_field2!=null?_field2.toString():this.getClass().getSimpleName());
	}


	/**
	 * Returns a deep clone of this {@link AE3ExpInterpreter} node.
	 * @return a deep clone of this {@link AE3ExpInterpreter} node
	 */
	public AE3ExpInterpreter clone()
	{
		return new AE3ExpInterpreter(
			cloneNode((AE1ExpInterpreter)_field1),
			cloneNode((AE2ExpInterpreter)_field2)
		);
	}


	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link AE3ExpInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE3ExpInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE3ExpInterpreter} node
	 */
	public void removeChild(INodeInterpreter child)
	{
		if (this._field1 == child) {
			this._field1 = null;
			return;
		}
		
		if (this._field2 == child) {
			this._field2 = null;
			return;
		}

		if (this.parent() == child) {
			this.parent( null);
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	 * Sets the {@code _field1} child of this {@link AE3ExpInterpreter} node.
	 * @param value the new {@code _field1} child of this {@link AE3ExpInterpreter} node
	*/
	public void setField1(AE1ExpInterpreter value)
	{
		if (_field1 != null) {
			_field1.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		_field1 = value;

	}


	/**
	 * @return the {@link AE1ExpInterpreter} node which is the {@code _field1} child of this {@link AE3ExpInterpreter} node
	*/
	public AE1ExpInterpreter getField1()
	{
		return (AE1ExpInterpreter)_field1;
	}
	
	/**
	 * Sets the {@code _field1} child of this {@link AE3ExpInterpreter} node.
	 * @param value the new {@code _field1} child of this {@link AE3ExpInterpreter} node
	*/
	public void setField2(AE2ExpInterpreter value)
	{
		if (_field2 != null) {
			_field2.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		_field2 = value;

	}


	/**
	 * @return the {@link AE1ExpInterpreter} node which is the {@code _field1} child of this {@link AE3ExpInterpreter} node
	*/
	public AE2ExpInterpreter getField2()
	{
		return (AE2ExpInterpreter)_field2;
	}


	/**
	* Calls the {@link IAnalysisInterpreter#caseAE3ExpInterpreter(AE3ExpInterpreter)} of the {@link IAnalysisInterpreter} {@code analysis}.
	* @param analysis the {@link IAnalysisInterpreter} to which this {@link AE3ExpInterpreter} node is applied
	*/
	@Override
	public void apply(IAnalysisInterpreter analysis)
	{
		analysis.caseAE3ExpInterpreter(this);
	}


	/**
	* Calls the {@link IAnswerInterpreter#caseAE3ExpInterpreter(AE3ExpInterpreter)} of the {@link IAnswerInterpreter} {@code caller}.
	* @param caller the {@link IAnswerInterpreter} to which this {@link AE3ExpInterpreter} node is applied
	*/
	@Override
	public <A> A apply(IAnswerInterpreter<A> caller)
	{
		return caller.caseAE3ExpInterpreter(this);
	}


	/**
	* Calls the {@link IQuestionInterpreter#caseAE3ExpInterpreter(AE3ExpInterpreter, Object)} of the {@link IQuestionInterpreter} {@code caller}.
	* @param caller the {@link IQuestionInterpreter} to which this {@link AE3ExpInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestionInterpreter<Q> caller, Q question)
	{
		caller.caseAE3ExpInterpreter(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswerInterpreter#caseAE3ExpInterpreter(AE3ExpInterpreter, Object)} of the {@link IQuestionAnswerInterpreter} {@code caller}.
	* @param caller the {@link IQuestionAnswerInterpreter} to which this {@link AE3ExpInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question)
	{
		return caller.caseAE3ExpInterpreter(this, question);
	}


	/**
	* Calls the {@link IAnalysis#caseAE3Exp(AE3Exp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AE3Exp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	/**
	* Calls the {@link IQuestion#caseAE3Exp(AE3Exp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AE3Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAE3Exp(AE3Exp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AE3Exp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	/**
	 * Returns the {@link EExpInterpreter} corresponding to the
	 * type of this {@link EExpInterpreter} node.
	 * @return the {@link EExpInterpreter} for this node
	 */
	@Override
	public EExpInterpreter kindPExpInterpreter()
	{
		return EExpInterpreter.E3;
	}



	public void parent(INode parent)
	{
//		this.p = parent;
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}



	public INodeInterpreter parent()
	{
		return (INodeInterpreter) super.parent();
	}


	@Override
	public NodeEnumInterpreter kindNodeInterpreter()
	{
		return NodeEnumInterpreter.EXP;
	}



	public void parent(INodeInterpreter parent)
	{
		super.parent( parent);
	}

//
//	/**
//	* Returns the nearest ancestor of this node (including itself)
//	* which is a subclass of {@code classType}.
//	* @param classType the superclass used
//	* @return the nearest ancestor of this node
//	*/
//	public INodeInterpreter getAncestor(Class<INode> classType)
//	{
//		INodeInterpreter n = this;
//		while (!classType.isInstance(n)) {
//			n = n.parent();
//			if (n == null) return null;
//		}
//		return n;
//	}



}
