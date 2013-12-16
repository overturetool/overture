package org.overture.typechecker.assistant.module;

import java.util.Collection;
import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.PExport;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PExportAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PExportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static Collection<? extends PDefinition> getDefinition(PExport exp,
			LinkedList<PDefinition> actualDefs)
	{
		try
		{
			return exp.apply(af.getExportDefinitionFinder(), actualDefs);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public static Collection<? extends PDefinition> getDefinition(PExport exp)
	{
		try
		{
			return exp.apply(af.getExportDefinitionListFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}
}
