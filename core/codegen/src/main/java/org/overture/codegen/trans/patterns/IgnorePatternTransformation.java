package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class IgnorePatternTransformation extends DepthFirstAnalysisAdaptor
{
	private int counter;
	private BaseTransformationAssistant baseAssistant;
	private final String NAME_PREFIX;
	
	public IgnorePatternTransformation(BaseTransformationAssistant baseAssistant, String namePrefix)
	{
		this.counter = 0;
		this.baseAssistant = baseAssistant;
		this.NAME_PREFIX = namePrefix;
	}

	@Override
	public void inAIgnorePatternCG(AIgnorePatternCG node)
			throws AnalysisException
	{
		String name = NAME_PREFIX + (++counter);
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);
		
		baseAssistant.replaceNodeWith(node, idPattern);
	}
}
