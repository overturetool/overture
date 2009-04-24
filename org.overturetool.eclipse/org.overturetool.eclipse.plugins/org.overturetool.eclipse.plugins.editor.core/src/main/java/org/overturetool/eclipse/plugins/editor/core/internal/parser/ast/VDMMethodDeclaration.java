package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.overturetool.ast.itf.IOmlAccessDefinition;
import org.overturetool.vdmj.definitions.AccessSpecifier;

public class VDMMethodDeclaration extends MethodDeclaration {
	public VDMMethodDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, AccessSpecifier specifier) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		this.setModifiers(AccessModifierConvert.getModifier(specifier));
	}
	
	public VDMMethodDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, int specifier) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		this.setModifiers(specifier);
	}
	
	public VDMMethodDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, IOmlAccessDefinition specifier) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		this.setModifiers(AccessModifierConvert.getOvertureModifier(specifier));
	}
}
