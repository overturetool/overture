
package org.overture.ast.statements;


import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import java.util.Map;
import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.node.INode;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.NodeEnum;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import org.overture.ast.expressions.EExpInterpreter;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AS1StmInterpreter extends PStmBaseInterpreter
{
	private static final long serialVersionUID = 1L;




	/**
	 * Creates a new {@link AE2ExpInterpreter} node with no children.
	 */
	public AS1StmInterpreter()
	{

	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AE2ExpInterpreter)		{
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
		return fields;
	}


	/**
	 * Returns the {@link EExpInterpreter} corresponding to the
	 * type of this {@link EExpInterpreter} node.
	 * @return the {@link EExpInterpreter} for this node
	 */
	@Override
	public EExpInterpreter kindPExpInterpreter()
	{
		return EExpInterpreter.E2;
	}


	/**
	 * Creates a deep clone of this {@link AE2ExpInterpreter} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AE2ExpInterpreter} node
	 */
	public AS1StmInterpreter clone(Map<INode,INode> oldToNewMap)
	{
		AS1StmInterpreter node = new AS1StmInterpreter(
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link AE2ExpInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE2ExpInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE2ExpInterpreter} node
	 */
	public void removeChild(INodeInterpreter child)
	{
		throw new RuntimeException("Not a child.");
	}


	/**
	 * Returns a deep clone of this {@link AE2ExpInterpreter} node.
	 * @return a deep clone of this {@link AE2ExpInterpreter} node
	 */
	public AS1StmInterpreter clone()
	{
		return new AS1StmInterpreter(
		);
	}



	public String toString()
	{
		return super.toString();
	}


	/**
	* Calls the {@link IAnalysisInterpreter#caseAE2ExpInterpreter(AS1StmInterpreter)} of the {@link IAnalysisInterpreter} {@code analysis}.
	* @param analysis the {@link IAnalysisInterpreter} to which this {@link AS1StmInterpreter} node is applied
	*/
	@Override
	public void apply(IAnalysisInterpreter analysis)
	{
		analysis.caseAS1StmInterpreter(this);
	}


	/**
	* Calls the {@link IAnswerInterpreter#caseAE2ExpInterpreter(AS1StmInterpreter)} of the {@link IAnswerInterpreter} {@code caller}.
	* @param caller the {@link IAnswerInterpreter} to which this {@link AS1StmInterpreter} node is applied
	*/
	@Override
	public <A> A apply(IAnswerInterpreter<A> caller)
	{
		return caller.caseAS1StmInterpreter(this);
	}


	/**
	* Calls the {@link IQuestionInterpreter#caseAS1StmInterpreter(AS1StmInterpreter, Object)} of the {@link IQuestionInterpreter} {@code caller}.
	* @param caller the {@link IQuestionInterpreter} to which this {@link AS1StmInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestionInterpreter<Q> caller, Q question)
	{
		caller.caseAS1StmInterpreter(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswerInterpreter#caseAS1StmInterpreter(AS1StmInterpreter, Object)} of the {@link IQuestionAnswerInterpreter} {@code caller}.
	* @param caller the {@link IQuestionAnswerInterpreter} to which this {@link AS1StmInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question)
	{
		return caller.caseAS1StmInterpreter(this, question);
	}


	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this {@link AE1ExpInterpreter} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AE1ExpInterpreter} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AE1ExpInterpreter} node
	 */
	public void removeChild(INode child)
	{
		if (parent() == child) {
			parent( null);
			return;
		}

		throw new RuntimeException("Not a child.");
	}





	@Override
	public NodeEnum kindNode()
	{
		throw new RuntimeException("Not allowed");
	}


	@Override
	public void parent(INode parent)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	@Override
	public void apply(IAnalysis analysis)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		throw new RuntimeException("this node should not be embedded in a non extended AST");
	}


	@Override
	public EStmInterpreter kindPStmInterpreter()
	{
		return EStmInterpreter.S1;
	}



}
