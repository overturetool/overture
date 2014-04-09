package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.utils.TempVarNameGen;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private PExpCG suchThat;
	private SSetTypeCG setType;
	
	public LetBeStStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant, PExpCG suchThat, SSetTypeCG setType)
	{
		super(config, transformationAssistant);
		
		String successVarNamePrefix = transformationAssistant.getVarPrefixes().getSuccessVarNamePrefix();
		TempVarNameGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();
		
		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		List<AVarLocalDeclCG> outerBlockDecls = new LinkedList<AVarLocalDeclCG>();
		
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
		return transformationAssistant.consForCondition(config.iteratorType(), iteratorName, successVarName, true, config.hasNextElement());
	}
	
	@Override
	public ABlockStmCG getForLoopBody(PExpCG set, AIdentifierPatternCG id,
			String iteratorName) throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementAssigned(config.iteratorType(), transformationAssistant.getSetTypeCloned(set).getSetOf(), id.getName(), iteratorName, config.nextElement());
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return packStm(transformationAssistant.consIfCheck(successVarName, config.runtimeExceptionTypeName(), "Let Be St found no applicable bindings"));
	}
}
