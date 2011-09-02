package org.overture.ast.modules.assistants;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.PExport;

public class AModuleExportsAssistant
{

	public static Collection<? extends PDefinition> getDefinitions(
			AModuleExports aModuleExports, LinkedList<PDefinition> actualDefs)
	{
		List<PDefinition> exportDefs = new Vector<PDefinition>();

		for (List<PExport> etype: aModuleExports.getExports())
		{
			for (PExport exp: etype)
			{
				exportDefs.addAll(PExportAssistant.getDefinition(exp,actualDefs));
			}
		}

		// Mark all exports as used

		for (PDefinition d: exportDefs)
		{
			PDefinitionAssistantTC.markUsed(d);
		}

		return exportDefs;
	}

}
