package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.overturetool.ast.imp.OmlScopeQuotes;
import org.overturetool.ast.itf.IOmlAccessDefinition;
import org.overturetool.vdmj.definitions.AccessSpecifier;

public class AccessModifierConvert {
	public static int getModifier(AccessSpecifier specifier) {
		try {
			int modifiers = 0;
			if(specifier.isStatic){
				modifiers |= TypeDeclaration.AccStatic;
			}
			if (specifier.access.name().equals("PRIVATE"))
			{
				modifiers |=  TypeDeclaration.AccPrivate;
			}
			if (specifier.access.name().equals("PROTECTED"))
			{
				modifiers |=  TypeDeclaration.AccProtected;
			}
			if (specifier.access.name().equals("PUBLIC"))
			{
				modifiers |=  TypeDeclaration.AccPublic;
			}
			return modifiers;
		} catch (Exception e) {
			System.out.println("Could not create field.. " + e.getMessage());
			return -666;
		}
	}

	public static int getOvertureModifier(IOmlAccessDefinition accessDefinition) {
		int modifiers = 0;
		
		try {
			if(accessDefinition.getStaticAccess().equals(Boolean.TRUE)){
				modifiers |= TypeDeclaration.AccStatic;
			}
			if (accessDefinition.getScope().getValue().equals(OmlScopeQuotes.IQPRIVATE))
			{
				modifiers |=  TypeDeclaration.AccPrivate;
			}
			if (accessDefinition.getScope().getValue().equals(OmlScopeQuotes.IQPROTECTED) ||
				accessDefinition.getScope().getValue().equals(OmlScopeQuotes.IQDEFAULT))
			{
				modifiers |=  TypeDeclaration.AccProtected;
			}
			if (accessDefinition.getScope().getValue().equals(OmlScopeQuotes.IQPUBLIC))
			{
				modifiers |=  TypeDeclaration.AccPublic;
			}
			return modifiers;
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
		
	}
}
