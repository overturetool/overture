package org.overture.ide.ui.dltk.outline;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.compiler.ISourceElementRequestor.FieldInfo;

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
public class VdmSourceElementRequestor extends SourceElementRequestVisitor {
	public VdmSourceElementRequestor(ISourceElementRequestor requestor) {
		super(requestor);
	}

	@Override
	public boolean visit(Statement statement) throws Exception {
		if (statement instanceof FieldDeclaration) {
			return addField((FieldDeclaration) statement);
		}
		return false;// TODO
	}

	@Override
	public boolean endvisit(Statement s) throws Exception {
		if (s instanceof FieldDeclaration) {
			return endField((FieldDeclaration) s);
		}
		return true;
	}

	private boolean addField(FieldDeclaration statement) {
		try {
			FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
			fieldInfo.name = statement.getName();
			fieldInfo.declarationStart = statement.sourceStart();
			fieldInfo.nameSourceStart = statement.getNameStart();
			fieldInfo.nameSourceEnd = statement.getNameEnd();
			fieldInfo.modifiers = statement.getModifiers();

			fRequestor.enterField(fieldInfo);

			return true;

		} catch (Exception e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}

	private boolean endField(FieldDeclaration statement) {

		fRequestor.exitField(statement.sourceEnd());
		return false;
	}
}
