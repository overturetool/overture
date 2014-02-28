package org.overture.codegen.transform;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.javalib.VDMSeq;

public class TransformationAssistantCG
{
	public SSetTypeCG getSetTypeCloned(PExpCG set)
			throws AnalysisException
	{
		PTypeCG typeCg = set.getType();

		return getSetTypeCloned(typeCg);
	}
	
	public SSetTypeCG getSetTypeCloned(PTypeCG typeCg) throws AnalysisException
	{
		if(!(typeCg instanceof SSetTypeCG))
			throw new AnalysisException("Exptected set type for set expression. Got: " + typeCg);
		
		SSetTypeCG setTypeCg = (SSetTypeCG) typeCg;
		
		return setTypeCg.clone();
	}
	
	public ALocalVarDeclCG consSetBindDecl(String setBindName, PExpCG set) throws AnalysisException
	{
		ALocalVarDeclCG setBindDecl = new ALocalVarDeclCG();
		
		setBindDecl.setType(getSetTypeCloned(set));
		setBindDecl.setName(setBindName);
		setBindDecl.setExp(set.clone());
		
		return setBindDecl;
	}
	
	public ALocalVarDeclCG consIdDecl(PTypeCG setType, String id) throws AnalysisException
	{
		ALocalVarDeclCG idDecl = new ALocalVarDeclCG();
		
		idDecl.setType(getSetTypeCloned(setType).getSetOf());
		idDecl.setName(id);
		idDecl.setExp(new ANullExpCG());
		
		return idDecl;
	}

	public ALocalVarDeclCG consDecl(String varName, PExpCG exp)
	{
		ALocalVarDeclCG resultDecl = new ALocalVarDeclCG();
		
		resultDecl.setType(exp.getType().clone());
		resultDecl.setName(varName);
		resultDecl.setExp(exp);
		
		return resultDecl;
	}
	
	public AIdentifierStateDesignatorCG consIdentifier(String name)
	{
		AIdentifierStateDesignatorCG identifier = new AIdentifierStateDesignatorCG();
		identifier.setName(name);

		return identifier;
	}
	
	public void replaceNodeWith(INode original, INode replacement)
	{
		INode parent = original.parent();
		parent.replaceChild(original, replacement);
		original.parent(null);
	}
	
	public AClassTypeCG consIteratorType()
	{
		return consClassType(IJavaCodeGenConstants.ITERATOR_TYPE);
	}
	
	public AClassTypeCG consClassType(String classTypeName)
	{
		AClassTypeCG iteratorType = new AClassTypeCG();
		iteratorType.setName(classTypeName);
		
		return iteratorType;
	}
	
	public PExpCG consInstanceCall(PTypeCG instanceType, String instanceName, PTypeCG returnType, String memberName, PExpCG arg)
	{
		AVariableExpCG instance = new AVariableExpCG();
		instance.setOriginal(instanceName);
		instance.setType(instanceType.clone());

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(memberName);
		fieldExp.setObject(instance);
		fieldExp.setType(returnType.clone());
		
		AApplyExpCG instanceCall = new AApplyExpCG();
		instanceCall.setRoot(fieldExp);
		instanceCall.setType(returnType.clone());
		
		if(arg != null)
		{
			instanceCall.getArgs().add(arg);
		}
			
		return instanceCall;
	}
	
	public ALocalVarDeclCG consIteratorDecl(String iteratorName, String collectionName)
	{
		ALocalVarDeclCG iterator = new ALocalVarDeclCG();
		iterator.setName(iteratorName);
		iterator.setType(consIteratorType());
		iterator.setExp(consInstanceCall(consClassType(VDMSeq.class.getName()), collectionName, consIteratorType(), IJavaCodeGenConstants.GET_ITERATOR , null));
		
		return iterator;
	}
}
