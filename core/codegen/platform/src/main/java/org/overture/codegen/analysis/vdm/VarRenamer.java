package org.overture.codegen.analysis.vdm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class VarRenamer
{
	public void rename(INode node, Set<Renaming> renamings)
			throws AnalysisException
	{
		node.apply(new RenameAnalysis(renamings));
	}

	public Set<Renaming> computeRenamings(List<? extends INode> nodes,
			ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
			throws AnalysisException
	{
		VarShadowingRenameCollector renamer = new VarShadowingRenameCollector(af, idDefs);

		for (INode node : nodes)
		{
			node.apply(renamer);
			renamer.init(false);
		}

		return renamer.getRenamings();
	}

	public Set<Renaming> computeRenamings(INode node,
			VarShadowingRenameCollector collector) throws AnalysisException
	{
		collector.init(true);
		node.apply(collector);

		return collector.getRenamings();
	}
}