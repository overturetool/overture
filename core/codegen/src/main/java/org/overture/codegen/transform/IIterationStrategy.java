package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.utils.ITempVarGen;

public interface IIterationStrategy
{
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids)
			throws AnalysisException;

	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	public PExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	public PExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException;

	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);

	public List<PStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids);

	public void setFirstBind(boolean firstBind);

	public void setLastBind(boolean lastBind);

}