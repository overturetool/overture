package org.overture.codegen.transform;

import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.javalib.VDMSeq;

public class TransformationAssistantCG
{
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
