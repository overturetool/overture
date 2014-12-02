package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;

public class CppLanguageIterator extends AbstractLanguageIterator {

	private static final String GET_ITERATOR = "iterator";
	private static final String NEXT_ELEMENT_ITERATOR = "next";
	private static final String HAS_NEXT_ELEMENT_ITERATOR = "hasNext";
	private static final String ITERATOR_TYPE = "iterator";

	protected String iteratorName;
	
	public CppLanguageIterator(
			TransformationAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes) {
		super(transformationAssistant, tempGen, varPrefixes);
		// TODO Auto-generated constructor stub

	}

	@Override
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern) {
		// TODO Auto-generated method stub
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		String setName = setVar.getOriginal();
		AClassTypeCG iteratorType = transformationAssistant.consClassType(ITERATOR_TYPE);
		STypeCG setType = setVar.getType().clone();
		SExpCG getIteratorCall = transformationAssistant.consInstanceCall(setType, setName, iteratorType.clone(), GET_ITERATOR, null);

		AVarDeclCG iteratorDecl = new AVarDeclCG();

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(iteratorName);

		iteratorDecl.setPattern(idPattern);
		iteratorDecl.setType(iteratorType);
		iteratorDecl.setExp(getIteratorCall);
		
		return iteratorDecl;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

}
