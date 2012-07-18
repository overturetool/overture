
package org.overture.ast.expressions;


import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;
import java.util.Map;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import org.overture.ast.expressions.EExpInterpreter;
import java.util.HashMap;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.analysis.intf.IAnalysisInterpreter;
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
public class AE1ExpInterpreter extends AE1Exp implements PExpInterpreter
{
	private static final long serialVersionUID = 1L;

//	private INodeInterpreter _parent;

	/**
	 * Creates a new {@link AE1ExpInterpreter} node with no children.
	 */
	public AE1ExpInterpreter()
	{

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
		fields.put("_parent",this.parent());
		return fields;
	}


//	/**
//	 * Returns the {@link EExp} corresponding to the
//	 * type of this {@link EExp} node.
//	 * @return the {@link EExp} for this node
//	 */
//	@Override
//	public EExp kindPExp()
//	{
//		return EExp.E1;
//	}


	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link AE1ExpInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE1ExpInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE1ExpInterpreter} node
	 */
	public void removeChild(INodeInterpreter child)
	{
		if (parent() == child) {
			parent( null);
			return;
		}

		throw new RuntimeException("Not a child.");
	}



	public String toString()
	{
		return super.toString();
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AE1ExpInterpreter)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Creates a deep clone of this {@link AE1ExpInterpreter} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE1ExpInterpreter} node
	 */
	public AE1ExpInterpreter clone(Map<INode,INode> oldToNewMap)
	{
		AE1ExpInterpreter node = new AE1ExpInterpreter(
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Returns a deep clone of this {@link AE1ExpInterpreter} node.
	 * @return a deep clone of this {@link AE1ExpInterpreter} node
	 */
	public AE1ExpInterpreter clone()
	{
		return new AE1ExpInterpreter(
		);
	}


	/**
	* Calls the {@link IAnalysisInterpreter#caseAE1ExpInterpreter(AE1ExpInterpreter)} of the {@link IAnalysisInterpreter} {@code analysis}.
	* @param analysis the {@link IAnalysisInterpreter} to which this {@link AE1ExpInterpreter} node is applied
	*/
	@Override
	public void apply(IAnalysisInterpreter analysis)
	{
		analysis.caseAE1Exp(this);
	}


	/**
	* Calls the {@link IAnswerInterpreter#caseAE1ExpInterpreter(AE1ExpInterpreter)} of the {@link IAnswerInterpreter} {@code caller}.
	* @param caller the {@link IAnswerInterpreter} to which this {@link AE1ExpInterpreter} node is applied
	*/
	@Override
	public <A> A apply(IAnswerInterpreter<A> caller)
	{
		return caller.caseAE1Exp(this);
	}


	/**
	* Calls the {@link IQuestionInterpreter#caseAE1ExpInterpreter(AE1ExpInterpreter, Object)} of the {@link IQuestionInterpreter} {@code caller}.
	* @param caller the {@link IQuestionInterpreter} to which this {@link AE1ExpInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestionInterpreter<Q> caller, Q question)
	{
		caller.caseAE1Exp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswerInterpreter#caseAE1ExpInterpreter(AE1ExpInterpreter, Object)} of the {@link IQuestionAnswerInterpreter} {@code caller}.
	* @param caller the {@link IQuestionAnswerInterpreter} to which this {@link AE1ExpInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question)
	{
		return caller.caseAE1Exp(this, question);
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


	/**
	 * Returns the {@link EExpInterpreter} corresponding to the
	 * type of this {@link EExpInterpreter} node.
	 * @return the {@link EExpInterpreter} for this node
	 */
	@Override
	public EExpInterpreter kindPExpInterpreter()
	{
		return EExpInterpreter.E1;
	}



	public void parent(INodeInterpreter parent)
	{
		super.parent( parent);
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


//	/**
//	* Returns the nearest ancestor of this node (including itself)
//	* which is a subclass of {@code classType}.
//	* @param classType the superclass used
//	* @return the nearest ancestor of this node
//	*/
//	@Override
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
