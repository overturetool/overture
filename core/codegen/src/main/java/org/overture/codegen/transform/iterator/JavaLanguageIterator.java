package org.overture.codegen.transform.iterator;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.ITransformationConfig;
import org.overture.codegen.transform.TransformationAssistantCG;
import org.overture.codegen.utils.ITempVarGen;

public class JavaLanguageIterator extends AbstractLanguageIterator
{
	public JavaLanguageIterator(ITransformationConfig config,
			TransformationAssistantCG transformationAssistant, ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(config, transformationAssistant, tempGen, varPrefixes);
	}

	protected String iteratorName;
		
	@Override
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		String setName = setVar.getOriginal();
		
		AClassTypeCG iteratorType = transformationAssistant.consClassType(config.iteratorType());
		AClassTypeCG setType = transformationAssistant.consClassType(config.setUtilFile());
		
		PExpCG getIteratorCall = transformationAssistant.consInstanceCall(setType, setName, iteratorType.clone(), config.iteratorMethod(), null);
		
		AVarLocalDeclCG iteratorDecl = new AVarLocalDeclCG();
		iteratorDecl.setName(iteratorName);
		iteratorDecl.setType(iteratorType);
		iteratorDecl.setExp(getIteratorCall);
		
		return iteratorDecl;
	}

	@Override
	public PExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssistant.consClassType(config.iteratorType());
		
		return transformationAssistant.consInstanceCall(iteratorType, iteratorName, new ABoolBasicTypeCG(), config.hasNextElement(), null);
	}

	@Override
	public PExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return null;
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return transformationAssistant.consNextElementDeclared(config.iteratorType(), transformationAssistant.getSetTypeCloned(setVar).getSetOf(), id.getName(), iteratorName, config.nextElement());
	}

	@Override
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return transformationAssistant.consNextElementAssignment(config.iteratorType(), transformationAssistant.getSetTypeCloned(setVar).getSetOf(), id.getName(), iteratorName, config.nextElement());
	}
}
