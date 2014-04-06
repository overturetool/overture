package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.utils.TempVarNameGen;

public abstract class AbstractIteratorStrategy extends AbstractIterationStrategy
{
	protected String iteratorName;

	public AbstractIteratorStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant)
	{
		super(config, transformationAssistant);
	}
	
	@Override
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
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
}