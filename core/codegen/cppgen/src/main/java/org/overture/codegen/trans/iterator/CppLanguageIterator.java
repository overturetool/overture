package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADeRefExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.APostIncExpCG;
import org.overture.codegen.cgast.expressions.APreIncExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class CppLanguageIterator extends JavaLanguageIterator{

	
	protected String iteratorName;
	
	public CppLanguageIterator(
			TransAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, tempGen, varPrefixes);
	}
	
//	@Override
//	public List<SStmCG> getPreForLoopStms(...)
//	{
//		List<SStmCG> stms = new LinkedList<SStmCG>();
//		
//		super.getPreForLoopStms()
//		
//		stms.add(super.getPreForLoopStms())
//	}

	@Override
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern) {
		// TODO Auto-generated method stub
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		AVarDeclCG iterator = new AVarDeclCG();
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(iteratorName);
		
		iterator.setPattern(idPattern);
		
		iterator.setType(setVar.getType().clone());
		//iterator.setExp(transformationAssistant.consInstanceCall(setVar.getType().clone(),
		// setVar.getOriginal(), iteratorType.clone(), config.iteratorMethod(),
		// null));
		iterator.setExp(transAssistant.consInstanceCall(setVar.getType().clone(),
				setVar.getName(), setVar.getType().clone(), "begin", null));
		return iterator;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException {
		
		ANotEqualsBinaryExpCG i_end_comp;

		i_end_comp = new ANotEqualsBinaryExpCG();

		// AVariableExpCG instance = new AVariableExpCG();
		AIdentifierVarExpCG instance = new AIdentifierVarExpCG();
		instance.setName(iteratorName);
		instance.setType(setVar.getType().clone());

		i_end_comp.setLeft(instance);

		i_end_comp.setRight(transAssistant.consInstanceCall( setVar.getType().clone(),
				setVar.getName(), new ABoolBasicTypeCG(), "end", null));

		return i_end_comp;
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern) {
		// TODO Auto-generated method stub
		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setName(iteratorName);
		var.setType(setVar.getType().clone());
		
		APreIncExpCG inc_exp = new APreIncExpCG();
		inc_exp.setType(setVar.getType().clone());
		inc_exp.setExp(var);

		return inc_exp;
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException {
		// TODO Auto-generated method stub
		AVarDeclCG cast = new AVarDeclCG();
		ADeRefExpCG deref_and_inc = new ADeRefExpCG();

		deref_and_inc.setType(setVar.getType().clone());

		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setName(iteratorName);
		var.setType(setVar.getType().clone());

		APostIncExpCG inc_exp = new APostIncExpCG();
		inc_exp.setType(setVar.getType().clone());
		inc_exp.setExp(var);

		deref_and_inc.setExp(var);
		
		ACastUnaryExpCG cast_to_value = new ACastUnaryExpCG();
		
		cast_to_value.setExp(deref_and_inc);
		STypeCG elementType = transAssistant.getSetTypeCloned(setVar).getSetOf();
		cast_to_value.setType(elementType);
		
		cast.setPattern(pattern);
		cast.setType(transAssistant.getSetTypeCloned(setVar).getSetOf());

		cast.setExp(deref_and_inc);

		return cast;
	}

//	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}



}
