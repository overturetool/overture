package org.overture.extract;

import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;

public class Extractor {
	
	public Set<Extraction> computeExtractions(List<? extends INode> nodes)
			throws AnalysisException
	{
		RefactoringExtractionCollector extractionCollector = new RefactoringExtractionCollector();

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
