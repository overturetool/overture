package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;

public class LambdaAssistant
{
	private List<AInterfaceDeclCG> lambdaInterfaces;

	public LambdaAssistant()
	{
		this.lambdaInterfaces = new LinkedList<AInterfaceDeclCG>();
	}
	
	public List<AInterfaceDeclCG> getLambdaInterfaces()
	{
		return lambdaInterfaces;
	}
	public void registerLambdaInterface(AInterfaceDeclCG lambdaInterface)
	{
		lambdaInterfaces.add(lambdaInterface);
	}
	
	public AInterfaceDeclCG findLambdaInterface(AMethodTypeCG methodType)
	{
		for(AInterfaceDeclCG lambdaInterface : lambdaInterfaces)
		{
			if(1 + methodType.getParams().size() == lambdaInterface.getTemplateTypes().size())
			{
				return lambdaInterface;
			}
		}

		return null;
	}
}
