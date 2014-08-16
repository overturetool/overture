package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;

public interface ILanguageIterator
{
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);

	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException;

	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);

	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException;

	public ALocalPatternAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern, AVarLocalDeclCG successVarDecl, AVarLocalDeclCG nextElementDecl)
			throws AnalysisException;

}