package org.overture.extract;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;

public class Extractor {
	

	public Set<Extraction> computeExtractions(INode node,
			RefactoringExtractionCollector collector) throws AnalysisException
	{
		collector.init(true);
		node.apply(collector);
		return collector.getExtractions();
	}
}
