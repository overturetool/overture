
package org.overture.ast.analysis;


import org.overture.ast.expressions.PExp;
import org.overture.ast.node.tokens.TInt;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.expressions.AE4Exp;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.node.IToken;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class QuestionAnswerAdaptor<Q, A> implements IQuestionAnswer<Q, A>
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link QuestionAnswerAdaptor} node with no children.
	 */
	public QuestionAnswerAdaptor()
	{

	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseTInt(TInt node, Q question)
	{
		return defaultIToken(node, question);
	}


	/**
	* Called by the {@link PExp} node from {@link PExp#apply(IAnalysis)}.
	* @param node the calling {@link PExp} node
	*/
	public A defaultPExp(PExp node, Q question)
	{
		return defaultINode(node, question);
	}


	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public A caseAE1Exp(AE1Exp node, Q question)
	{
		return defaultPExp(node, question);
	}


	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public A caseAE3Exp(AE3Exp node, Q question)
	{
		return defaultPExp(node, question);
	}


	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public A caseAE4Exp(AE4Exp node, Q question)
	{
		return defaultPExp(node, question);
	}


	/**
	* Called by the {@link INode} node from {@link INode#apply(IAnalysis)}.
	* @param node the calling {@link INode} node
	*/
	public A defaultINode(INode node, Q question)
	{
		return null;//nothing to do
	}


	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A defaultIToken(IToken node, Q question)
	{
		return null;//nothing to do
	}



}
