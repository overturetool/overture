package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

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
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public AVarLocalDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return langIterator.getForLoopInit(setVar, patterns, pattern);
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return langIterator.getForLoopCond(setVar, patterns, pattern);
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return langIterator.getForLoopInc(setVar, patterns, pattern);
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return langIterator.getNextElementDeclared(setVar, patterns, pattern);
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return null;
	}

	@Override
	public List<SStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns)
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