package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;

public abstract class AbstractIterationStrategy
{
	abstract public List<? extends SLocalDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException;

	public AVarLocalDeclCG getForLoopInit(String iteratorName, String setTypeName, String setName,
			String getIteratorMethod)
	{
		return transformationAssistant.consIteratorDecl(config.iteratorType(), iteratorName, setTypeName, setName, getIteratorMethod);
	}

	abstract public PExpCG getForLoopCond(String iteratorName) throws AnalysisException;

	public PExpCG getForLoopInc(String iteratorName)
	{
		return null;
	}
	
	abstract public ABlockStmCG getForLoopBody(PExpCG set, AIdentifierPatternCG id, String iteratorName) throws AnalysisException;

	abstract public List<PStmCG> getLastForLoopStms();
	
	abstract public List<PStmCG> getOuterBlockStms();
	
	protected ITransformationConfig config;
	protected TransformationAssistantCG transformationAssistant;
	
	protected boolean firstBind;
	protected boolean lastBind;
	
	public AbstractIterationStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant)
	{
		this.config = config;
		this.transformationAssistant = transformationAssistant;
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
