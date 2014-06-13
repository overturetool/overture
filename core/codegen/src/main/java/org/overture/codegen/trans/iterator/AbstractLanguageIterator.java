package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;

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
	abstract public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	@Override
	abstract public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
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
