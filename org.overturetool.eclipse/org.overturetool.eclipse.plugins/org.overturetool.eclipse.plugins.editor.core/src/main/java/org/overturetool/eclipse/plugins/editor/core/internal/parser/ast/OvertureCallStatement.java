package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.statements.Statement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.DLTKConverter;
import org.overturetool.vdmj.statements.CallStatement;


public class OvertureCallStatement extends Statement {
	CallStatement callStatement;
	
	public OvertureCallStatement(CallStatement callStatement, DLTKConverter converter) {
		this.callStatement = callStatement;
		this.setStart(converter.convert(callStatement.location.startLine, callStatement.location.startPos));
		this.setEnd(converter.convert(callStatement.location.endLine, callStatement.location.endPos));
	}
	
	@Override
	public int getKind() {
		return S_EMPTY; //TODO ?
	}
	
	@Override
	public void traverse(ASTVisitor visitor) throws Exception {
		super.traverse(visitor);
	}
}
