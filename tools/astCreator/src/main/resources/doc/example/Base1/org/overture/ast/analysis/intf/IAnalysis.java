
package org.overture.ast.analysis.intf;


import org.overture.ast.node.tokens.TInt;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.expressions.AE4Exp;
import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE3Exp;
import java.io.Serializable;
import org.overture.ast.node.IToken;


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
public interface IAnalysis extends Serializable
{	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public void caseTInt(TInt node);
	/**
	* Called by the {@link AE1Exp} node from {@link AE1Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE1Exp} node
	*/
	public void caseAE1Exp(AE1Exp node);
	/**
	* Called by the {@link AE3Exp} node from {@link AE3Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE3Exp} node
	*/
	public void caseAE3Exp(AE3Exp node);
	/**
	* Called by the {@link AE4Exp} node from {@link AE4Exp#apply(IAnalysis)}.
	* @param node the calling {@link AE4Exp} node
	*/
	public void caseAE4Exp(AE4Exp node);

}
