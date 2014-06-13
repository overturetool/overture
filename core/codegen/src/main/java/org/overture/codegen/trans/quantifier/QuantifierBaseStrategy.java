package org.overture.codegen.trans.quantifier;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class QuantifierBaseStrategy extends AbstractIterationStrategy
{
	protected SExpCG predicate;
	protected String resultVarName;

	public QuantifierBaseStrategy(
			TransformationAssistantCG transformationAssistant,
			SExpCG predicate, String resultVarName,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);

		this.predicate = predicate;
		this.resultVarName = resultVarName;
	}
}