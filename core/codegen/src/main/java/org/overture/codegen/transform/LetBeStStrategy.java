package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.utils.TempVarNameGen;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private LetBeStAssistantCG letBeStAssistant;
	private PExpCG suchThat;
	private SSetTypeCG setType;

	public LetBeStStrategy(TempVarNameGen tempVarGen,
			LetBeStAssistantCG letBeStAssistant, PExpCG suchThat, SSetTypeCG setType)
	{
		super();
		this.successVarName = tempVarGen.nextVarName(JavaTempVarPrefixes.SUCCESS_VAR_NAME_PREFIX);
		this.letBeStAssistant = letBeStAssistant;
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		List<ALocalVarDeclCG> outerBlockDecls = new LinkedList<ALocalVarDeclCG>();
		
		for(AIdentifierPatternCG id : ids)
		{
			outerBlockDecls.add(letBeStAssistant.consIdDecl(setType, id.getName()));
		}
		
		outerBlockDecls.add(letBeStAssistant.consBoolVarDecl(successVarName, false));
		
		return outerBlockDecls;
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName) throws AnalysisException
	{
		return letBeStAssistant.conForCondition(iteratorName, successVarName, true);
	}
	
	@Override
	public ABlockStmCG getForLoopBody(PTypeCG setElementType, AIdentifierPatternCG id,
			String iteratorName) throws AnalysisException
	{
		return letBeStAssistant.consForBodyNextElementAssigned(setElementType, id.getName(), iteratorName);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return packStm(letBeStAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return packStm(letBeStAssistant.consIfCheck(successVarName));
	}
}
