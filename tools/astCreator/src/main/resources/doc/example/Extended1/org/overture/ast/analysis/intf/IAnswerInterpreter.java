
package org.overture.ast.analysis.intf;



import java.io.Serializable;

import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.node.ITokenInterpreter;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.statements.AS1StmInterpreter;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public interface IAnswerInterpreter<A> extends Serializable, IAnswer<A>
{	/**
	* Called by the {@link ITokenInterpreter} node from {@link ITokenInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link ITokenInterpreter} node
	*/
	public A caseTIntInterpreter(TIntInterpreter node);
//	/**
//	* Called by the {@link AE1ExpInterpreter} node from {@link AE1ExpInterpreter#apply(IAnalysisInterpreter)}.
//	* @param node the calling {@link AE1ExpInterpreter} node
//	*/
//	public A caseAE1ExpInterpreter(AE1ExpInterpreter node);
	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public A caseAE2ExpInterpreter(AE2ExpInterpreter node);
	
	/**
	* Called by the {@link AE3ExpInterpreter} node from {@link AE3ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE3ExpInterpreter} node
	*/
	public A caseAE3ExpInterpreter(AE3ExpInterpreter node);
	
	/**
	* Called by the {@link AE3ExpInterpreter} node from {@link AE3ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE3ExpInterpreter} node
	*/
	public A caseAE4ExpInterpreter(AE4ExpInterpreter node);
	
	/**
	* Called by the {@link AE2ExpInterpreter} node from {@link AE2ExpInterpreter#apply(IAnalysisInterpreter)}.
	* @param node the calling {@link AE2ExpInterpreter} node
	*/
	public A caseAS1StmInterpreter(AS1StmInterpreter node);

}
