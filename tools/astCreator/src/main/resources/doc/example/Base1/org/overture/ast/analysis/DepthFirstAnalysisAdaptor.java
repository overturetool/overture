
package org.overture.ast.analysis;


import java.util.ArrayList;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.expressions.AE4Exp;
import java.util.List;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
import java.util.Set;
import org.overture.ast.node.IToken;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
@SuppressWarnings({"rawtypes","unchecked"})
public class DepthFirstAnalysisAdaptor implements IAnalysis
{
	private static final long serialVersionUID = 1L;

	protected Set _queue = new java.util.HashSet<INode>();

	/**
	* Creates a new {@code DepthFirstAnalysisAdaptor} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param queue_ the {@link Set} node for the {@code queue} child of this {@link DepthFirstAnalysisAdaptor} node
	*/
	public DepthFirstAnalysisAdaptor(Set queue_)
	{
		super();
		this.setQueue(queue_);

	}


	/**
	 * Creates a new {@link DepthFirstAnalysisAdaptor} node with no children.
	 */
	public DepthFirstAnalysisAdaptor()
	{

	}


	/**
	 * Sets the {@code _queue} child of this {@link DepthFirstAnalysisAdaptor} node.
	 * @param value the new {@code _queue} child of this {@link DepthFirstAnalysisAdaptor} node
	*/
	public void setQueue(Set value)
	{
		this._queue = value;

	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void caseTInt(TInt node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inTInt(node);


		outTInt(node);
		_queue.remove(node);

	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void inTInt(TInt node)
	{
		defaultInIToken(node);
	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void outTInt(TInt node)
	{
		defaultOutIToken(node);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public void defaultInPExp(PExp node)
	{
		defaultInINode(node);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public void defaultOutPExp(PExp node)
	{
		defaultOutINode(node);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public void inPExp(PExp node)
	{
		defaultInINode(node);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public void outPExp(PExp node)
	{
		defaultOutINode(node);
	}


	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public void caseAE1Exp(AE1Exp node)
	{
		if(_queue.contains(node))
		{ //already visiting this node from other path
			return;
		}
		_queue.add(node);
		inAE1Exp(node);


		outAE1Exp(node);
		_queue.remove(node);

	}


	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public void inAE1Exp(AE1Exp node)
	{
		defaultInPExp(node);
	}


	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public void outAE1Exp(AE1Exp node)
	{
		defaultOutPExp(node);
	}


	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public void caseAE3Exp(AE3Exp node)
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

		outAE3Exp(node);
		_queue.remove(node);

	}


	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public void inAE3Exp(AE3Exp node)
	{
		defaultInPExp(node);
	}


	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public void outAE3Exp(AE3Exp node)
	{
		defaultOutPExp(node);
	}


	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public void caseAE4Exp(AE4Exp node)
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


	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public void inAE4Exp(AE4Exp node)
	{
		defaultInPExp(node);
	}


	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public void outAE4Exp(AE4Exp node)
	{
		defaultOutPExp(node);
	}


	/**
	* Called by the {@link INode} node from {@link INode#apply(IAnalysis)}.
	* @param node the calling {@link INode} node
	*/
	public void defaultOutINode(INode node)
	{
		//nothing to do
	}


	/**
	* Called by the {@link INode} node from {@link INode#apply(IAnalysis)}.
	* @param node the calling {@link INode} node
	*/
	public void defaultInINode(INode node)
	{
		//nothing to do
	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void defaultOutIToken(IToken node)
	{
		//nothing to do
	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void defaultInIToken(IToken node)
	{
		//nothing to do
	}



}
