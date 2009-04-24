package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.overturetool.ast.itf.IOmlAccessDefinition;
import org.overturetool.vdmj.definitions.AccessSpecifier;

public class VDMFieldDeclaration  extends FieldDeclaration {
	
	public VDMFieldDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, AccessSpecifier specifier) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		this.setModifiers(AccessModifierConvert.getModifier(specifier));
	}
	
	public VDMFieldDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd, int specifier) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		this.setModifiers(specifier);
	}
	
	public VDMFieldDeclaration(String name, int nameStart, int nameEnd, int declStart, int declEnd,  IOmlAccessDefinition accessDef) {
		super(name, nameStart, nameEnd, declStart, declEnd);
		AccessModifierConvert.getOvertureModifier(accessDef);
		
	}

}
