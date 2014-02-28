package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected TransformationAssistantCG transformationAssitant;
	
	public CompStrategy(TransformationAssistantCG transformationAssitant)
	{
		super();
		this.transformationAssitant = transformationAssitant;
	}
	
	public abstract PTypeCG getCollectionType() throws AnalysisException;
	public abstract String getVar();
	public abstract String getClassName();
	public abstract String getMemberName();
	public abstract PTypeCG getElementType() throws AnalysisException;
	public abstract PExpCG getFirst();
	public abstract PExpCG getPredicate();
	
	
	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls() throws AnalysisException
	{
		PTypeCG setType = getCollectionType();
		String varDeclName = getVar();
		String className = getClassName();
		String memberName = getMemberName();
		
		return packDecl(transformationAssitant.consCompResultDecl(setType, varDeclName, className, memberName));
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssitant.consIteratorType();
		PTypeCG elementType = getElementType();
		
		return transformationAssitant.consInstanceCall(iteratorType, iteratorName, elementType, IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		String varDeclName = getVar();
		PExpCG first = getFirst();
		PExpCG predicate = getPredicate();
		
		return packStm(transformationAssitant.consConditionalAdd(varDeclName, first, predicate));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;//Indicates that there are no extra statements to be added to the outer block
	}
}
