package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.utils.TempVarNameGen;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private LetBeStAssistantCG letBeStAssistant;
	private PExpCG suchThat;

	public LetBeStStrategy(TempVarNameGen tempVarGen,
			LetBeStAssistantCG letBeStAssistant, PExpCG suchThat)
	{
		super();
		this.successVarName = tempVarGen.nextVarName(JavaTempVarPrefixes.SUCCESS_VAR_NAME_PREFIX);
		this.letBeStAssistant = letBeStAssistant;
		this.suchThat = suchThat;
	}

	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls()
	{
		return packDecl(letBeStAssistant.consSuccessVarDecl(successVarName));
	}

	@Override
	public PExpCG getForLoopCond(SSetTypeCG setType, String iteratorName) throws AnalysisException
	{
		return letBeStAssistant.conForCondition(setType, iteratorName, successVarName);
	}

	@Override
	public List<PStmCG> getCurrentForLoopStms()
	{
		return packStm(letBeStAssistant.consSuccessAssignment(suchThat, successVarName));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return packStm(letBeStAssistant.consIfCheck(successVarName));
	}

}
