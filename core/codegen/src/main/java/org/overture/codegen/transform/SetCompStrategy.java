package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SetCompStrategy extends AbstractIterationStrategy
{
	private TransformationAssistantCG transformationAssitant;
	private ACompSetExpCG setComp;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant, ACompSetExpCG setComp)
	{
		super();
		this.transformationAssitant = transformationAssitant;
		this.setComp = setComp;
	}

	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls() throws AnalysisException
	{
		SSetTypeCG setType = transformationAssitant.getSetTypeCloned(setComp.getSet());
		String varDeclName = setComp.getVar();
		
		return packDecl(transformationAssitant.consCompResultDecl(setType, varDeclName, IJavaCodeGenConstants.SET_UTIL_FILE, IJavaCodeGenConstants.SET_UTIL_EMPTY_SET_CALL));
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transformationAssitant.consIteratorType();
		SSetTypeCG setType = transformationAssitant.getSetTypeCloned(setComp.getSet());
		
		return transformationAssitant.consInstanceCall(iteratorType, iteratorName, setType.getSetOf(), IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR, null);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		String varDeclName = setComp.getVar();
		PExpCG first = setComp.getFirst();
		PExpCG predicate = setComp.getPredicate();
		
		return packStm(transformationAssitant.consConditionalAdd(varDeclName, first, predicate));
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;//Indicates that there are no extra statements to be added to the outer block
	}

}
