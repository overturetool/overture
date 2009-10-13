package org.overture.ide.vdmpp.ui.internal.outline;

import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;

/***
 * Class used to customize the model build for the outline
 * 
 * @author Kenneth Lausdahl <!-- customize the outline to include fileds etc.
 *         --><br>
 *         <extension<br>
 *         point="org.eclipse.dltk.core.sourceElementParsers"><br>
 *         <parser<br>
 *         class="org.overture.ide.vdmrt.ui.internal.outline.SourceElementParser"
 * <br>
 *         nature="org.overture.ide.vdmrt.core.nature"<br>
 *         priority="0"><br>
 *         </parser><br>
 *         </extension><br>
 * 
 */
public class VdmPpSourceElementRequestor extends SourceElementRequestVisitor {
	public VdmPpSourceElementRequestor(ISourceElementRequestor requestor) {
		super(requestor);
	}

	@Override
	public boolean visit(Statement statement) throws Exception {
//		System.out.println("Source element parser visiting: "
//				+ statement.debugString());

		return false;// TODO
	}

	@Override
	public boolean endvisit(Statement s) throws Exception {
		return true;
	}
}
