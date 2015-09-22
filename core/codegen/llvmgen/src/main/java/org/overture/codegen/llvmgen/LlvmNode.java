package org.overture.codegen.llvmgen;

public class LlvmNode
{
	private String data;

	public LlvmNode(String data)
	{
		this.data = data;
	}
	
	@Override
	public String toString()
	{
		return "Phony LLVM node: " + data;
	}
}
