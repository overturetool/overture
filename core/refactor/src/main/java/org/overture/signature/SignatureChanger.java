package org.overture.signature;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.extract.Extraction;
import org.overture.extract.RefactoringExtractionCollector;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SignatureChanger {
	public Set<SignatureChange> computeSignatures(List<? extends INode> nodes,
			ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
			throws AnalysisException
	{
		RefactoringSignatureChangeCollector signatureChangeCollector = new RefactoringSignatureChangeCollector(af, idDefs);

		for (INode node : nodes)
		{
			node.apply(signatureChangeCollector);
			signatureChangeCollector.init(false);
		}

		return signatureChangeCollector.getSignatureChanges();
	}
	
	public Set<SignatureChange> computeSignatureChanges(INode node,
			RefactoringSignatureChangeCollector collector) throws AnalysisException
	{
		collector.init(true);
		node.apply(collector);

		return collector.getSignatureChanges();
	}
}
