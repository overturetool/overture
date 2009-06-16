package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;

public class OvertureSelectionParser extends OvertureAssistParser {
	public void handleNotInElement(ASTNode node, int position) {
	}

	public void parseBlockStatements(ASTNode node, ASTNode inNode, int position) {
	}

	public ModuleDeclaration getModule() {
		return module;
	}
}
