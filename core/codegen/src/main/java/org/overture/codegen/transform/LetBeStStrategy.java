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
	private PExpCG suchThat;
	private SSetTypeCG setType;

	public LetBeStStrategy(TempVarNameGen tempVarGen,
			TransformationAssistantCG transformationAssistant, PExpCG suchThat, SSetTypeCG setType)
	{
		super(transformationAssistant);
		this.successVarName = tempVarGen.nextVarName(JavaTempVarPrefixes.SUCCESS_VAR_NAME_PREFIX);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		List<ALocalVarDeclCG> outerBlockDecls = new LinkedList<ALocalVarDeclCG>();
		
		for(AIdentifierPatternCG id : ids)
		{
			outerBlockDecls.add(transformationAssistant.consIdDecl(setType, id.getName()));
		}
		
		outerBlockDecls.add(transformationAssistant.consBoolVarDecl(successVarName, false));
		
		return outerBlockDecls;
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName) throws AnalysisException
	{
		return transformationAssistant.conForCondition(iteratorName, successVarName, true);
	}
	
	@Override
	public ABlockStmCG getForLoopBody(PTypeCG setElementType, AIdentifierPatternCG id,
			String iteratorName) throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementAssigned(setElementType, id.getName(), iteratorName);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return packStm(transformationAssistant.consIfCheck(successVarName, "Let Be St found no applicable bindings"));
	}
}
