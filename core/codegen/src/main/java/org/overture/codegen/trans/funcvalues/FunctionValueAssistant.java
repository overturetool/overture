package org.overture.codegen.trans.funcvalues;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;

public class FunctionValueAssistant
{
	private List<AInterfaceDeclCG> functionValueInterfaces;

	public FunctionValueAssistant()
	{
		this.functionValueInterfaces = new LinkedList<AInterfaceDeclCG>();
	}
	
	public List<AInterfaceDeclCG> getFunctionValueInterfaces()
	{
		return functionValueInterfaces;
	}
	public void registerInterface(AInterfaceDeclCG functionValueInterface)
	{
		functionValueInterfaces.add(functionValueInterface);
	}
	
	public AInterfaceDeclCG findInterface(AMethodTypeCG methodType)
	{
		for(AInterfaceDeclCG functionValueInterface : functionValueInterfaces)
		{
			if(1 + methodType.getParams().size() == functionValueInterface.getTemplateTypes().size())
			{
				return functionValueInterface;
			}
		}

		return null;
	}
}
