package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.statements.Statement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.DLTKConverter;

public class OvertureStatement extends Statement {
	
	
	public OvertureStatement(org.overturetool.vdmj.statements.Statement vdmjStatement, DLTKConverter converter) {
		this.setStart(converter.convert(vdmjStatement.location.startLine, vdmjStatement.location.startPos));
		this.setEnd(converter.convert(vdmjStatement.location.endLine, vdmjStatement.location.endPos));
	}
	
	@Override
	public int getKind() {
		return S_EMPTY; //TODO ?
	}

}
