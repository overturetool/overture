package org.overture.codegen.transform.iterator;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.TransformationAssistantCG;
import org.overture.codegen.utils.ITempVarGen;

public abstract class AbstractLanguageIterator implements ILanguageIterator
{
	protected TransformationAssistantCG transformationAssistant;
	protected ITempVarGen tempGen;
	protected TempVarPrefixes varPrefixes;

	public AbstractLanguageIterator(
			TransformationAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		this.transformationAssistant = transformationAssistant;
		this.tempGen = tempGen;
		this.varPrefixes = varPrefixes;
	}

	@Override
	abstract public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	@Override
	abstract public PExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	@Override
	abstract public PExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	@Override
	abstract public AVarLocalDeclCG getNextElementDeclared(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids,
			AIdentifierPatternCG id) throws AnalysisException;

	@Override
	abstract public AAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids,
			AIdentifierPatternCG id) throws AnalysisException;
}
