package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.overturetool.vdmj.definitions.AccessSpecifier;

public class VDMClassDeclaration extends TypeDeclaration {

	//VDMJ
	public VDMClassDeclaration(String name, int nameStart, int nameEnd, int start, int end, AccessSpecifier specifier) {
		super(name, nameStart, nameEnd, start, end);
		this.setModifiers(AccessModifierConvert.getModifier(specifier));
	}

	//OVERTURE
	public VDMClassDeclaration(String name, int nameStart, int nameEnd, int start, int end, int specifier) {
		super(name, nameStart, nameEnd, start, end);
		this.setModifiers(specifier);
	}
	
}
