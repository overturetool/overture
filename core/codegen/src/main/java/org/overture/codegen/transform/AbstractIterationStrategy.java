package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public abstract class AbstractIterationStrategy implements IIterationStrategy
{
	protected boolean firstBind;
	protected boolean lastBind;

	protected TransformationAssistantCG transformationAssistant;
	protected ILanguageIterator langIterator;
	protected ITempVarGen tempGen;
	protected TempVarPrefixes varPrefixes;

	public AbstractIterationStrategy(TransformationAssistantCG transformationAssistant,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		this.transformationAssistant = transformationAssistant;
		this.langIterator = langIterator;
		this.tempGen = tempGen;
		this.varPrefixes = varPrefixes;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return langIterator.getForLoopInit(setVar, ids, id);
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getForLoopCond(setVar, ids, id);
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return langIterator.getForLoopInc(setVar, ids, id);
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getNextElementDeclared(setVar, ids, id);
	}

	@Override
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return null;
	}

	@Override
	public List<SStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids)
	{
		return null;
	}

	@Override
	public void setFirstBind(boolean firstBind)
	{
		this.firstBind = firstBind;
	}

	@Override
	public void setLastBind(boolean lastBind)
	{
		this.lastBind = lastBind;
	}

	protected List<SStmCG> packStm(SStmCG stm)
	{
		List<SStmCG> stms = new LinkedList<SStmCG>();

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