package org.overture.extract;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.refactoring.RefactoringLogger;

public class Extractor {
	

	public RefactoringLogger<Extraction> computeExtractions(INode node,
			RefactoringExtractionCollector collector) throws AnalysisException
	{
		collector.init(true);
		node.apply(collector);
		return collector.getRefactoringLogger();
	}
}
