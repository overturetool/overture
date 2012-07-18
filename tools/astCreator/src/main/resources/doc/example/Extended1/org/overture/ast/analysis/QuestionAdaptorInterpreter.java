
package org.overture.ast.analysis;


import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.expressions.PExpInterpreter;
import org.overture.ast.node.INodeInterpreter;
import org.overture.ast.node.ITokenInterpreter;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.statements.AS1StmInterpreter;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class QuestionAdaptorInterpreter<Q> extends QuestionAdaptor<Q> implements IQuestionInterpreter<Q>
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link QuestionAdaptorInterpreter} node with no children.
	 */
	public QuestionAdaptorInterpreter()
	{

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void caseTIntInterpreter(TIntInterpreter node, Q question)
	{
		defaultITokenInterpreter(node, question);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void defaultPExpInterpreter(PExpInterpreter node, Q question)
	{
		defaultPExp(node, question);
	}




	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public void caseAE2ExpInterpreter(AE2ExpInterpreter node, Q question)
	{
		defaultPExpInterpreter(node, question);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public void defaultINodeInterpreter(INodeInterpreter node, Q question)
	{
		defaultINode(node, question);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void defaultITokenInterpreter(ITokenInterpreter node, Q question)
	{
		defaultIToken(node, question);
	}


	@Override
	public void caseAE3ExpInterpreter(AE3ExpInterpreter node, Q question)
	{
		defaultPExpInterpreter(node, question);
	}
	
	@Override
	public void caseAE4ExpInterpreter(AE4ExpInterpreter node, Q question)
	{
		defaultPExpInterpreter(node, question);
	}


	@Override
	public void caseAS1StmInterpreter(AS1StmInterpreter node, Q question)
	{
		defaultPStmInterpreter(node, question);
	}


	private void defaultPStmInterpreter(AS1StmInterpreter node, Q question)
	{
		defaultINodeInterpreter(node, question);
		
	}



}
