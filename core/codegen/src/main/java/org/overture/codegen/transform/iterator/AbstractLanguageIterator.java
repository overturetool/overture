package org.overture.codegen.transform.iterator;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.ITransformationConfig;
import org.overture.codegen.transform.TransformationAssistantCG;
import org.overture.codegen.utils.TempVarNameGen;

public abstract class AbstractLanguageIterator
{
	protected ITransformationConfig config;
	protected TransformationAssistantCG transformationAssistant;
	
	public AbstractLanguageIterator(ITransformationConfig config, TransformationAssistantCG transformationAssistant)
	{
		this.config = config;
		this.transformationAssistant = transformationAssistant;
	}
	
	abstract public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	abstract public PExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	abstract public PExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);
	

	abstract public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;
	
	abstract public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;
}
