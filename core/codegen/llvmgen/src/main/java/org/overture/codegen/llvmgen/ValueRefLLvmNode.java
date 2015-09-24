package org.overture.codegen.llvmgen;

import org.robovm.llvm.binding.ValueRef;

public class ValueRefLLvmNode extends LlvmNode
{

	public final ValueRef valueRef;

	public ValueRefLLvmNode(ValueRef data)
	{
		super("");
		this.valueRef =data;
	}
	
	
	

}
