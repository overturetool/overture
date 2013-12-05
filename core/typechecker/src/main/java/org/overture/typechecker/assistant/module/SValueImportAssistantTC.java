package org.overture.typechecker.assistant.module;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SValueImportAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SValueImportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
//	public static List<PDefinition> getDefinitions(SValueImport imp,
//			AModuleModules module) {
//
//		List<PDefinition> list = new Vector<PDefinition>();
//		imp.setFrom(module);
//		ILexNameToken name = imp.getName();
//		
//		PDefinition expdef = PDefinitionListAssistantTC.findName(module.getExportdefs(),name, NameScope.NAMES);
//
//		if (expdef == null)
//		{
//			TypeCheckerErrors.report(3193, "No export declared for import of value " + name + " from " + module.getName(),imp.getLocation(),imp);
//		}
//		else
//		{
//			if (imp.getRenamed() != null)
//			{
//				expdef = AstFactory.newARenamedDefinition(imp.getRenamed(), expdef);
//			}
//			else
//			{
//				expdef = AstFactory.newAImportedDefinition(imp.getLocation(), expdef);
//			}
//
//			list.add(expdef);
//		}
//
//		return list;
//	}

}
