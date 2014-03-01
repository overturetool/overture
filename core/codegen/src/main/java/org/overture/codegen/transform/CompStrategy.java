package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected TransformationAssistantCG transformationAssitant;
	protected PExpCG first;
	protected PExpCG predicate;
	protected PExpCG set;
	protected String var;
	
	public abstract String getClassName();
	public abstract String getMemberName();
	public abstract PTypeCG getCollectionType() throws AnalysisException;
	
	public CompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, PExpCG set, String var)
	{
		super();
		this.transformationAssitant = transformationAssitant;
		this.first = first;
		this.predicate = predicate;
		this.set = set;
		this.var = var;
	}
	
	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		String className = getClassName();
		String memberName = getMemberName();
		PTypeCG collectionType = getCollectionType();
		
		return packDecl(transformationAssitant.consCompResultDecl(collectionType, var, className, memberName));
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssitant.consIteratorType();
		PTypeCG elementType = transformationAssitant.getSetTypeCloned(set).getSetOf();
		
		return transformationAssitant.consInstanceCall(iteratorType, iteratorName, elementType, IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null);
	}

	@Override
	public ABlockStmCG getForLoopBody(AIdentifierPatternCG id, String iteratorName) throws AnalysisException
	{
		return transformationAssitant.consForBodyNextElementDeclared(set.getType(), id.getName(), iteratorName);
	}
	
	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return packStm(transformationAssitant.consConditionalAdd(var, first, predicate));
	}
	
	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;//Indicates that there are no extra statements to be added to the outer block
	}
}
