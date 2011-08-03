package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.typecheck.TypeCheckerErrors;

public class ATypeImportAssistant {

	public static List<PDefinition> getDefinitions(ATypeImport imp,
			AModuleModules module) {
		
		List<PDefinition> list = new Vector<PDefinition>();
		imp.setFrom(module);
		PDefinition expdef = PDefinitionListAssistant.findType(imp.getFrom().getExportdefs(),imp.getName(), null);

		if (expdef == null)
		{
			TypeCheckerErrors.report(3191, "No export declared for import of type " + imp.getName() + " from " + imp.getFrom().getName(),imp.getLocation(),imp);
		}
		else
		{
			if (imp.getRenamed() != null)
			{
				expdef = new ARenamedDefinition(imp.getRenamed().location, imp.getRenamed(), expdef.getNameScope(), false, PAccessSpecifierAssistant.getDefault(), null, expdef);
			}
			else
			{
				expdef = new AImportedDefinition(imp.getName().location, expdef.getNameScope(), false, null, PAccessSpecifierAssistant.getDefault(), null, expdef,expdef.getName());
			}

			list.add(expdef);
		}

		return list;
	}

}
