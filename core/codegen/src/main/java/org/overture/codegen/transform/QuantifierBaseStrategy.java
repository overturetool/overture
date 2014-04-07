package org.overture.codegen.transform;

import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.transform.iterator.AbstractLanguageIterator;

public abstract class QuantifierBaseStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String resultVarName;

	public QuantifierBaseStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant, PExpCG predicate, String resultVarName, AbstractLanguageIterator langIterator)
	{
		super(config, transformationAssistant, langIterator);
		
		this.predicate = predicate;
		this.resultVarName = resultVarName;
	}
}