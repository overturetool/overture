package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AImportedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AImportedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static void markUsed(AImportedDefinition d)
//	{
//		d.setUsed(true);
//		PDefinitionAssistantTC.markUsed(d.getDef());
//
//	}

	 public static boolean isUsed(AImportedDefinition u) {
	 return PDefinitionAssistantTC.isUsed(u.getDef());
	
	 }

}
