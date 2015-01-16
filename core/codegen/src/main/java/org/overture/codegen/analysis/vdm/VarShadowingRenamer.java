package org.overture.codegen.analysis.vdm;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class VarShadowingRenamer
{
	public void rename(SClassDefinition clazz, List<Renaming> renamings)
			throws AnalysisException
	{
		clazz.apply(new RenameAnalysis(renamings));
	}

	public List<Renaming> computeRenamings(List<SClassDefinition> classes,
			ITypeCheckerAssistantFactory af) throws AnalysisException
	{
		VarShadowingRenameCollector renamer = new VarShadowingRenameCollector(af);

		for (SClassDefinition clazz : classes)
		{
			clazz.apply(renamer);
			renamer.init(false);
		}

		return renamer.getRenamings();
	}

	public List<Renaming> computeRenamings(SClassDefinition clazz,
			VarShadowingRenameCollector collector) throws AnalysisException
	{
		collector.init(true);
		clazz.apply(collector);

		return collector.getRenamings();
	}
}