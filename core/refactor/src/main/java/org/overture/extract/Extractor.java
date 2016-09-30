package org.overture.extract;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.rename.RefactoringRenameCollector;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class Extractor {
	
	public Set<Extraction> computeExtractions(List<? extends INode> nodes,
			ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
			throws AnalysisException
	{
		RefactoringExtractionCollector extractionCollector = new RefactoringExtractionCollector(af, idDefs);

		for (INode node : nodes)
		{
			node.apply(extractionCollector);
			extractionCollector.init(false);
		}

		return extractionCollector.getExtractions();
	}
	
	public Set<Extraction> computeExtractions(INode node,
			RefactoringExtractionCollector collector) throws AnalysisException
	{
		collector.init(true);
		node.apply(collector);

		return collector.getExtractions();
	}
}
