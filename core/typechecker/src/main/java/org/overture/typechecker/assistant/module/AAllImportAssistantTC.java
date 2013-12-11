package org.overture.typechecker.assistant.module;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AAllImportAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAllImportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
//	public static List<PDefinition> getDefinitions(AAllImport imp,
//			AModuleModules module) {
//		imp.setFrom(module);
//
//		if (imp.getFrom().getExportdefs().isEmpty())
//		{
//			TypeCheckerErrors.report(3190, "Import all from module with no exports?",imp.getLocation(),imp);
//		} 
//
//		List<PDefinition> imported = new Vector<PDefinition>() ;
//
//		for (PDefinition d: imp.getFrom().getExportdefs())
// {
//			PDefinition id = AstFactory.newAImportedDefinition(
//					imp.getLocation(), d);
//			PDefinitionAssistantTC.markUsed(id); // So imports all is quiet
//			imported.add(id);
//
//		}
//
//		return imported;	// The lot!
//	}

}
