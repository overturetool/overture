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
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.TempVarNameGen;

public abstract class AbstractIterationStrategy
{
	protected boolean firstBind;
	protected boolean lastBind;

	protected ITransformationConfig config;
	protected TransformationAssistantCG transformationAssistant;
	protected ILanguageIterator langIterator;
	
	public AbstractIterationStrategy(ITransformationConfig config,
			TransformationAssistantCG transformationAssistant, ILanguageIterator langIterator)
	{
		this.config = config;
		this.transformationAssistant = transformationAssistant;
		this.langIterator = langIterator;
	}

	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, TempVarNameGen tempGen,
			TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		return null;
	}

	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return langIterator.getForLoopInit(setVar, tempGen, varPrefixes, ids, id);
	}

	public PExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getForLoopCond(setVar, tempGen, varPrefixes, ids, id);
	}

	public PExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return langIterator.getForLoopInc(setVar, tempGen, varPrefixes, ids, id);
	}

	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getNextElementDeclared(setVar, tempGen, varPrefixes, ids, id);
	}
	
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return null;
	}

	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return null;
	}

	public List<PStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			TempVarNameGen tempGen, TempVarPrefixes varPrefixes,
			List<AIdentifierPatternCG> ids)
	{
		return null;
	}
	
	public void setFirstBind(boolean firstBind)
	{
		this.firstBind = firstBind;
	}

	public void setLastBind(boolean lastBind)
	{
		this.lastBind = lastBind;
	}
	
	protected List<PStmCG> packStm(PStmCG stm)
	{
		List<PStmCG> stms = new LinkedList<PStmCG>();
		
		stms.add(stm);
		
		return stms;
	}
	
	protected List<SLocalDeclCG> packDecl(SLocalDeclCG decl)
	{
		List<SLocalDeclCG> decls = new LinkedList<SLocalDeclCG>();
		
		decls.add(decl);
		
		return decls;
	}
}