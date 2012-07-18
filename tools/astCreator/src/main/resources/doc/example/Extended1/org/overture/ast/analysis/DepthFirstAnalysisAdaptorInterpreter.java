
package org.overture.ast.analysis;


import java.util.Set;

import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.expressions.PExpInterpreter;
import org.overture.ast.node.INode;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.ITokenInterpreter;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.statements.AS1StmInterpreter;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
@SuppressWarnings({"rawtypes","unchecked"})
public class DepthFirstAnalysisAdaptorInterpreter extends DepthFirstAnalysisAdaptor implements IAnalysisInterpreter
{
	private static final long serialVersionUID = 1L;

	protected Set _queue = new java.util.HashSet<INode>();

	/**
	* Creates a new {@code DepthFirstAnalysisAdaptorInterpreter} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param queue_ the {@link Set} node for the {@code queue} child of this {@link DepthFirstAnalysisAdaptorInterpreter} node
	*/
	public DepthFirstAnalysisAdaptorInterpreter(Set queue_)
	{
		super();
		this.setQueue(queue_);

	}


	/**
	 * Creates a new {@link DepthFirstAnalysisAdaptorInterpreter} node with no children.
	 */
	public DepthFirstAnalysisAdaptorInterpreter()
	{

	}


	/**
	 * Sets the {@code _queue} child of this {@link DepthFirstAnalysisAdaptorInterpreter} node.
	 * @param value the new {@code _queue} child of this {@link DepthFirstAnalysisAdaptorInterpreter} node
	*/
	public void setQueue(Set value)
	{
		this._queue = value;

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void caseTIntInterpreter(TIntInterpreter node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inTIntInterpreter(node);


		outTIntInterpreter(node);
		_queue.remove(node);

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void inTIntInterpreter(TIntInterpreter node)
	{
		defaultInITokenInterpreter(node);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void outTIntInterpreter(TIntInterpreter node)
	{
		defaultOutITokenInterpreter(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void defaultInPExpInterpreter(PExpInterpreter node)
	{
		defaultInPExp(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void defaultOutPExpInterpreter(PExpInterpreter node)
	{
		defaultOutPExp(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void inPExpInterpreter(PExpInterpreter node)
	{
		defaultInPExp(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void outPExpInterpreter(PExpInterpreter node)
	{
		defaultOutPExp(node);
	}






	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public void caseAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inAE2ExpInterpreter(node);


		outAE2ExpInterpreter(node);
		_queue.remove(node);

	}


	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public void inAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		defaultInPExpInterpreter(node);
	}


	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public void outAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		defaultOutPExpInterpreter(node);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public void defaultOutINodeInterpreter(INodeInterpreter node)
	{
		defaultOutINode(node);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public void defaultInINodeInterpreter(INodeInterpreter node)
	{
		defaultInINode(node);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void defaultOutITokenInterpreter(ITokenInterpreter node)
	{
		defaultOutIToken(node);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void defaultInITokenInterpreter(ITokenInterpreter node)
	{
		defaultInIToken(node);
	}


	@Override
	public void caseAE3ExpInterpreter(AE3ExpInterpreter node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inAE3Exp(node);

		if(node.getField1() != null) {
			node.getField1().apply(this);
		}
		
		if(node.getField2() != null) {
			node.getField2().apply(this);
		}

		outAE3Exp(node);
		_queue.remove(node);
	}
	
	@Override
	public void caseAE4ExpInterpreter(AE4ExpInterpreter node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inAE4Exp(node);

		
		if(node.getField2() != null) {
			node.getField2().apply(this);
		}

		outAE4Exp(node);
		_queue.remove(node);
	}


	@Override
	public void caseAS1StmInterpreter(AS1StmInterpreter node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inAS1StmInterpreter(node);


		outAS1StmInterpreter(node);
		_queue.remove(node);
	}


	private void outAS1StmInterpreter(AS1StmInterpreter node)
	{
		defaultOutPStmInterpreter(node);
	}


	private void defaultOutPStmInterpreter(AS1StmInterpreter node)
	{
		defaultOutINodeInterpreter(node);
	}


	private void inAS1StmInterpreter(AS1StmInterpreter node)
	{
		defaultInPStmInterpreter(node);
	}


	private void defaultInPStmInterpreter(AS1StmInterpreter node)
	{
		defaultInINodeInterpreter(node);
	}



}
