package org.overture.codegen.analysis.vdm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class VarRenamer
{
	public void rename(SClassDefinition clazz, Set<Renaming> renamings)
			throws AnalysisException
	{
		clazz.apply(new RenameAnalysis(renamings));
	}

	public Set<Renaming> computeRenamings(List<SClassDefinition> classes,
			ITypeCheckerAssistantFactory af, Map<AIdentifierStateDesignator, PDefinition> idDefs) throws AnalysisException
	{
		VarShadowingRenameCollector renamer = new VarShadowingRenameCollector(af, idDefs);

		for (SClassDefinition clazz : classes)
		{
			clazz.apply(renamer);
			renamer.init(false);
		}

		return renamer.getRenamings();
	}

	public Set<Renaming> computeRenamings(SClassDefinition clazz,
			VarShadowingRenameCollector collector) throws AnalysisException
	{
		collector.init(true);
		clazz.apply(collector);

		return collector.getRenamings();
	}
}