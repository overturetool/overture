
package org.overture.ast.analysis;


import org.overture.ast.expressions.PExp;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.expressions.AE4Exp;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.node.IToken;
import org.overture.ast.analysis.intf.IQuestion;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class QuestionAdaptor<Q> implements IQuestion<Q>
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link QuestionAdaptor} node with no children.
	 */
	public QuestionAdaptor()
	{

	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void caseTInt(TInt node, Q question)
	{
		defaultIToken(node, question);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public void defaultPExp(PExp node, Q question)
	{
		defaultINode(node, question);
	}


	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public void caseAE1Exp(AE1Exp node, Q question)
	{
		defaultPExp(node, question);
	}


	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public void caseAE3Exp(AE3Exp node, Q question)
	{
		defaultPExp(node, question);
	}


	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public void caseAE4Exp(AE4Exp node, Q question)
	{
		defaultPExp(node, question);
	}


	/**
	* Called by the {@link INode} node from {@link INode#apply(IAnalysis)}.
	* @param node the calling {@link INode} node
	*/
	public void defaultINode(INode node, Q question)
	{
		//nothing to do
	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void defaultIToken(IToken node, Q question)
	{
		//nothing to do
	}



}
