
package org.overture.ast.analysis;


import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
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
public class AnswerAdaptorInterpreter<A> extends AnswerAdaptor<A> implements IAnswerInterpreter<A>
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link AnswerAdaptorInterpreter} node with no children.
	 */
	public AnswerAdaptorInterpreter()
	{

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public A caseTIntInterpreter(TIntInterpreter node)
	{
		return defaultITokenInterpreter(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public A defaultPExpInterpreter(PExpInterpreter node)
	{
		return defaultPExp(node);
	}




	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public A caseAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		return defaultPExpInterpreter(node);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public A defaultINodeInterpreter(INodeInterpreter node)
	{
		return defaultINode(node);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public A defaultITokenInterpreter(ITokenInterpreter node)
	{
		return defaultIToken(node);
	}


	@Override
	public A caseAE3ExpInterpreter(AE3ExpInterpreter node)
	{
		return defaultPExpInterpreter(node);
	}
	
	@Override
	public A caseAE4ExpInterpreter(AE4ExpInterpreter node)
	{
		return defaultPExpInterpreter(node);
	}


	@Override
	public A caseAS1StmInterpreter(AS1StmInterpreter node)
	{
		return defaultPStmInterpreter(node);
	}


	private A defaultPStmInterpreter(AS1StmInterpreter node)
	{
		return defaultINodeInterpreter(node);
	}



}
