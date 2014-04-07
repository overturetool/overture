package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private PExpCG suchThat;
	private SSetTypeCG setType;
	
	public LetBeStStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant, PExpCG suchThat, SSetTypeCG setType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(config, transformationAssistant, langIterator, tempGen, varPrefixes);
		
		String successVarNamePrefix = transformationAssistant.getVarPrefixes().getSuccessVarNamePrefix();
		ITempVarGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();
		
		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids) throws AnalysisException
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
	public PExpCG getForLoopCond(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id) throws AnalysisException
	{
		PExpCG left = langIterator.getForLoopCond(setVar, tempGen, varPrefixes, ids, id);
		PExpCG right = transformationAssistant.consBoolCheck(successVarName, true);
		
		return transformationAssistant.consAndExp(left, right);
	}
	
	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return null;
	}
	
	@Override
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, tempGen, varPrefixes, ids, id);
	}
	
	@Override
	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<PStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
	{
		return packStm(transformationAssistant.consIfCheck(successVarName, config.runtimeExceptionTypeName(), "Let Be St found no applicable bindings"));
	}
}
