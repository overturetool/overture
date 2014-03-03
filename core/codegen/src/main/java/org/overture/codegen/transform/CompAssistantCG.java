package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.utils.TempVarNameGen;

public class CompAssistantCG extends TransformationAssistantCG
{
	public CompAssistantCG()
	{
	}

	public ACastUnaryExpCG consNextElementCall(String instance, String member, ACompSeqExpCG seqComp) throws AnalysisException
	{
		
		PTypeCG elementType = getSeqTypeCloned(seqComp).getSeqOf();
		
		PExpCG nextCall = consInstanceCall(consIteratorType() , instance, elementType.clone(), member , null);
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(elementType.clone());
		cast.setExp(nextCall);
		
		return cast;
	}

	public ALocalVarDeclCG consSetBindIdDecl(String instanceName, String memberName, ACompSeqExpCG seqComp) throws AnalysisException
	{
		SSeqTypeCG seqType = getSeqTypeCloned(seqComp);
		
		PTypeCG elementType = seqType.getSeqOf();
		String id = seqComp.getId().getName();
		ACastUnaryExpCG initExp = consNextElementCall(instanceName, memberName, seqComp);
		
		ALocalVarDeclCG idDecl = new ALocalVarDeclCG();
		idDecl.setType(elementType);
		idDecl.setName(id);
		idDecl.setExp(initExp);
		
		return idDecl;
	}
	
	public ALocalVarDeclCG consSetBindDecl(String setBindName, ACompSeqExpCG seqComp) throws AnalysisException
	{	
		ASetSetTypeCG setType = new ASetSetTypeCG();
		setType.setSetOf(getSeqTypeCloned(seqComp).getSeqOf());
		
		ALocalVarDeclCG setBindDecl = new ALocalVarDeclCG();
		
		setBindDecl.setType(setType);
		setBindDecl.setName(setBindName);
		setBindDecl.setExp(seqComp.getSet());
		
		return setBindDecl;
	}
	
	public ALocalVarDeclCG consResultSeqDecl(String varDeclName, ACompSeqExpCG seqComp) throws AnalysisException
	{
		return consCompResultDecl(getSeqTypeCloned(seqComp), varDeclName, IJavaCodeGenConstants.SEQ_UTIL_FILE, IJavaCodeGenConstants.SEQ_UTIL_EMPTY_SEQ_CALL);
	}
	
	public ABlockStmCG consForBody(ACompSeqExpCG seqComp, String iteratorName,
			String resSeqName) throws AnalysisException
	{
		ABlockStmCG forBody = new ABlockStmCG();
		forBody.getLocalDefs().add(consSetBindIdDecl(iteratorName, IJavaCodeGenConstants.NEXT_ELEMENT_ITERATOR, seqComp));
		forBody.getStatements().add(consConditionalAdd(IJavaCodeGenConstants.ADD_ELEMENT_TO_LIST, resSeqName, seqComp.getPredicate(), seqComp.getFirst()));
		
		return forBody;
	}
	
	public ABlockStmCG consSetCompIterationBlock(List<ASetMultipleBindCG> multipleSetBinds, PExpCG predicate, TempVarNameGen tempGen, ComplexCompStrategy strategy) throws AnalysisException
	{
		ABlockStmCG outerBlock = new ABlockStmCG();
		
		ABlockStmCG nextMultiBindBlock = outerBlock;
		
		strategy.setFirstBind(true);
		
		for(int i = 0; i < multipleSetBinds.size(); i++)
		{
			strategy.setLastBind(i == multipleSetBinds.size() - 1);
			
			ASetMultipleBindCG mb = multipleSetBinds.get(i);
			nextMultiBindBlock = consIterationBlock(nextMultiBindBlock, mb.getPatterns(), mb.getSet(), predicate, tempGen, strategy);
			
			strategy.setFirstBind(false);
		}
		
		return outerBlock;
	}
}
