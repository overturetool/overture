package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;

public class OvertureModuleDeclaration extends ModuleDeclaration {
	
	public OvertureModuleDeclaration(int sourceLength) {
		super(sourceLength);
	}
	
	public OvertureModuleDeclaration(int sourceLength, boolean rebuild) {
		super(sourceLength, rebuild);
	} 
}
