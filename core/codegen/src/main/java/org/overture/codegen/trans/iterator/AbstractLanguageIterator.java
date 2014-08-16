package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
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
			List<SPatternCG> patterns, SPatternCG pattern);

	@Override
	abstract public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException;

	@Override
	abstract public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);

	@Override
	abstract public AVarLocalDeclCG getNextElementDeclared(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern) throws AnalysisException;

	@Override
	abstract public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarLocalDeclCG successVarDecl, AVarLocalDeclCG nextElementDecl) throws AnalysisException;
}
