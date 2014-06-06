package org.overture.codegen.transform.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.TransformationAssistantCG;
import org.overture.codegen.utils.ITempVarGen;

public class JavaLanguageIterator extends AbstractLanguageIterator
{
	private static final String GET_ITERATOR = "iterator";
	private static final String NEXT_ELEMENT_ITERATOR = "next";
	private static final String HAS_NEXT_ELEMENT_ITERATOR = "hasNext";
	private static final String ITERATOR_TYPE = "Iterator";

	public JavaLanguageIterator(
			TransformationAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, tempGen, varPrefixes);
	}

	protected String iteratorName;

	@Override
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		String setName = setVar.getOriginal();
		AClassTypeCG iteratorType = transformationAssistant.consClassType(ITERATOR_TYPE);
		STypeCG setType = setVar.getType().clone();
		SExpCG getIteratorCall = transformationAssistant.consInstanceCall(setType, setName, iteratorType.clone(), GET_ITERATOR, null);

		AVarLocalDeclCG iteratorDecl = new AVarLocalDeclCG();
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(iteratorName);
		
		iteratorDecl.setPattern(idPattern);
		iteratorDecl.setType(iteratorType);
		iteratorDecl.setExp(getIteratorCall);

		return iteratorDecl;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssistant.consClassType(ITERATOR_TYPE);

		return transformationAssistant.consInstanceCall(iteratorType, iteratorName, new ABoolBasicTypeCG(), HAS_NEXT_ELEMENT_ITERATOR, null);
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return null;
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		STypeCG elementType = transformationAssistant.getSetTypeCloned(setVar).getSetOf();
		String name = id.getName();

		return transformationAssistant.consNextElementDeclared(ITERATOR_TYPE, elementType, name, iteratorName, NEXT_ELEMENT_ITERATOR);
	}

	@Override
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		STypeCG elementType = transformationAssistant.getSetTypeCloned(setVar).getSetOf();
		String name = id.getName();

		return transformationAssistant.consNextElementAssignment(ITERATOR_TYPE, elementType, name, iteratorName, NEXT_ELEMENT_ITERATOR);
	}
}
