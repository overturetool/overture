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
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private SExpCG suchThat;
	private SSetTypeCG setType;
	
	int count = 0;
	private List<AVarLocalDeclCG> decls = new LinkedList<AVarLocalDeclCG>();
	
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
			AVarLocalDeclCG decl = transformationAssistant.consIdDecl(setType, id);
			decls.add(decl);
			outerBlockDecls.add(decl);
		}

		successVarDecl = transformationAssistant.consBoolVarDecl(successVarName, false);
		outerBlockDecls.add(successVarDecl);

		return outerBlockDecls;
	}
	
	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		if(count > 0)
		{
			ALocalAssignmentStmCG successAssignment = new ALocalAssignmentStmCG();
			successAssignment.setExp(transformationAssistant.getInfo().getExpAssistant().consBoolLiteral(false));
			successAssignment.setTarget(transformationAssistant.consSuccessVar(successVarName));
			
			return packStm(successAssignment);
		}
		else
		{
			return null;
		}
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
	public DeclarationTag consDeclarationTag()
	{
		return new DeclarationTag(true, successVarDecl);
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AVarLocalDeclCG nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);
		return null;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, patterns, pattern, successVarDecl, this.nextElementDeclared);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<SStmCG> getPostOuterBlockStms(AIdentifierVarExpCG setVar,
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
