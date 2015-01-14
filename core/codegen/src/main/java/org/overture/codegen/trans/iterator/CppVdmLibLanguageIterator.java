package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADeRefExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.APostIncExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class CppVdmLibLanguageIterator implements ILanguageIterator {

	private String iteratorName;
	private String isEndName;
	private ITempVarGen tempGen;
	private TempVarPrefixes varPrefixes;
	private TransAssistantCG transformationAssistant;

	public CppVdmLibLanguageIterator(TransAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes) {
		//super(transformationAssistant, tempGen, varPrefixes);
		// TODO Auto-generated constructor stub
		this.tempGen = tempGen;
		this.varPrefixes = varPrefixes;
		this.transformationAssistant = transformationAssistant;
	}
	
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		AVarDeclCG iteratorDecl = new AVarDeclCG();
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		isEndName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		
		/*
		 * Create set_var.First(iterator_var)
		 * */
		AIdentifierVarExpCG as = new AIdentifierVarExpCG();
		try {
			as.setType(transformationAssistant.getSetTypeCloned(setVar).getSetOf());
		} catch (AnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		as.setOriginal(iteratorName);
		
		
		SExpCG getFirstElemExp = transformationAssistant.consInstanceCall(setVar.getType().clone(), 
				setVar.getOriginal(), 
				new ABoolBasicTypeCG(), 
				"First", as);
		
		/*final Pattern
		 * 
		 * Create bool isEnd = set_var.First(iterator_var)
		 * */
		AIdentifierPatternCG endName = new AIdentifierPatternCG();
		endName.setName(isEndName);

		iteratorDecl.setPattern(endName);
		iteratorDecl.setType(new ABoolBasicTypeCG());
		iteratorDecl.setExp(getFirstElemExp);

		return iteratorDecl;
	}

	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AIdentifierVarExpCG isEndVar = new AIdentifierVarExpCG();
		isEndVar.setType(new ABoolBasicTypeCG());
		isEndVar.setOriginal(isEndName);
		
		ABoolLiteralExpCG tr = new ABoolLiteralExpCG();
		tr.setType(new ABoolBasicTypeCG());
		tr.setValue(true);
		
		AEqualsBinaryExpCG eq = new AEqualsBinaryExpCG();
		eq.setLeft(isEndVar);
		eq.setRight(tr);
		return eq;
	}

	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		//AAssignmentStmCG
		return null;
	}

	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AVarDeclCG outer = new AVarDeclCG();
		
		AIdentifierVarExpCG iterVar = new AIdentifierVarExpCG();
		iterVar.setType(transformationAssistant.getSetTypeCloned(setVar).getSetOf());
		iterVar.setOriginal(iteratorName);
		
//		SExpCG getNext = transformationAssistant.consInstanceCall(setVar.getType().clone(), 
//				setVar.getOriginal(),
//				transformationAssistant.getSetTypeCloned(setVar).getSetOf(), 
//				"Next", iterVar);
//		
		ACastUnaryExpCG cast_to_value = new ACastUnaryExpCG();
		cast_to_value.setExp(iterVar);
		cast_to_value.setType(transformationAssistant.getSetTypeCloned(setVar).getSetOf());
		outer.setPattern(pattern);
		outer.setExp(cast_to_value);
		outer.setType(transformationAssistant.getSetTypeCloned(setVar).getSetOf());
		
		return outer;
	}

	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException
	{
//		AIdentifierVarExpCG as = new AIdentifierVarExpCG();
//		try {
//			as.setType(transformationAssistant.getSetTypeCloned(setVar).getSetOf());
//		} catch (AnalysisException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		as.setOriginal(iteratorName);
//		SExpCG getFirstElemExp = transformationAssistant.consInstanceCall(setVar.getType().clone(), 
//				setVar.getOriginal(), 
//				new ABoolBasicTypeCG(), 
//				"Next", as);
//		
//		ALocalPatternAssignmentStmCG next = new ALocalPatternAssignmentStmCG();
//		next.setExp(getFirstElemExp);
//		next.setTarget(pattern);
		return null;
	}
	

}
