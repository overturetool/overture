
package org.overture.ast.analysis;


import org.overture.ast.analysis.intf.IAnalysisInterpreter;
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
public class AnalysisAdaptorInterpreter extends AnalysisAdaptor implements IAnalysisInterpreter
{
	private static final long serialVersionUID = 1L;



	/**
	 * Creates a new {@link AnalysisAdaptorInterpreter} node with no children.
	 */
	public AnalysisAdaptorInterpreter()
	{

	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void caseTIntInterpreter(TIntInterpreter node)
	{
		defaultITokenInterpreter(node);
	}


	/**
	* Called by the {@link PExpInterpreter} node from {@link PExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link PExpInterpreter} node
	*/
	public void defaultPExpInterpreter(PExpInterpreter node)
	{
		defaultPExp(node);
	}




	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public void caseAE2ExpInterpreter(AE2ExpInterpreter node)
	{
		defaultPExpInterpreter(node);
	}


	/**
	* Called by the {@link INodeInterpreter} node from {@link INodeInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link INodeInterpreter} node
	*/
	public void defaultINodeInterpreter(INodeInterpreter node)
	{
		defaultINode(node);
	}


	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public void defaultITokenInterpreter(ITokenInterpreter node)
	{
		defaultIToken(node);
	}


	@Override
	public void caseAE3ExpInterpreter(AE3ExpInterpreter node)
	{
		defaultPExpInterpreter(node);
	}

	@Override
	public void caseAE4ExpInterpreter(AE4ExpInterpreter node)
	{
		defaultPExpInterpreter(node);
	}

	@Override
	public void caseAS1StmInterpreter(AS1StmInterpreter node)
	{
		defaultPStmInterpreter(node);
		
	}


	private void defaultPStmInterpreter(AS1StmInterpreter node)
	{
		defaultINodeInterpreter(node);
	}



}
