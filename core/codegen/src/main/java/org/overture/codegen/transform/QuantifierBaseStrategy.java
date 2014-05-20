package org.overture.codegen.transform;

import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public abstract class QuantifierBaseStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String resultVarName;

	public QuantifierBaseStrategy(
			TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);

		this.predicate = predicate;
		this.resultVarName = resultVarName;
	}
}