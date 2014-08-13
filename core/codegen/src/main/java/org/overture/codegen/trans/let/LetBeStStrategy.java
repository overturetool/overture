package org.overture.codegen.trans.let;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStNoBindingRuntimeErrorExpCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private SExpCG suchThat;
	private SSetTypeCG setType;

	public LetBeStStrategy(TransformationAssistantCG transformationAssistant,
			SExpCG suchThat, SSetTypeCG setType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);

		String successVarNamePrefix = transformationAssistant.getVarPrefixes().getSuccessVarNamePrefix();
		ITempVarGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();

		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		List<AVarLocalDeclCG> outerBlockDecls = new LinkedList<AVarLocalDeclCG>();

		for (SPatternCG id : patterns)
		{
			outerBlockDecls.add(transformationAssistant.consIdDecl(setType, id));
		}

		outerBlockDecls.add(transformationAssistant.consBoolVarDecl(successVarName, false));

		return outerBlockDecls;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, patterns, pattern);
		SExpCG right = transformationAssistant.consBoolCheck(successVarName, true);

		return transformationAssistant.consAndExp(left, right);
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, patterns, pattern);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<SStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns)
	{
		ALetBeStNoBindingRuntimeErrorExpCG noBinding = new ALetBeStNoBindingRuntimeErrorExpCG();
		noBinding.setType(new AErrorTypeCG());

		ARaiseErrorStmCG raise = new ARaiseErrorStmCG();
		raise.setError(noBinding);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(transformationAssistant.consBoolCheck(successVarName, true));
		ifStm.setThenStm(raise);

		return packStm(ifStm);
	}
}
