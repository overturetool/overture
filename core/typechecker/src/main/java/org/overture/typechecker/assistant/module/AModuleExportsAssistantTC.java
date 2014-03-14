package org.overture.typechecker.assistant.module;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.PExport;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AModuleExportsAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AModuleExportsAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public Collection<? extends PDefinition> getDefinitions(
			AModuleExports aModuleExports, LinkedList<PDefinition> actualDefs)
	{
		List<PDefinition> exportDefs = new Vector<PDefinition>();

		for (List<PExport> etype : aModuleExports.getExports())
		{
			for (PExport exp : etype)
			{
				exportDefs.addAll(af.createPExportAssistant().getDefinition(exp, actualDefs));
			}
		}

		// Mark all exports as used

		for (PDefinition d : exportDefs)
		{
			af.createPDefinitionAssistant().markUsed(d);
		}

		return exportDefs;
	}

	public Collection<? extends PDefinition> getDefinitions(
			AModuleExports aModuleExports)
	{
		List<PDefinition> exportDefs = new Vector<PDefinition>();

		for (List<PExport> etype : aModuleExports.getExports())
		{
			for (PExport exp : etype)
			{
				exportDefs.addAll(af.createPExportAssistant().getDefinition(exp));
			}
		}

		// Mark all exports as used

		for (PDefinition d : exportDefs)
		{
			af.createPDefinitionAssistant().markUsed(d);
		}

		return exportDefs;
	}

}
