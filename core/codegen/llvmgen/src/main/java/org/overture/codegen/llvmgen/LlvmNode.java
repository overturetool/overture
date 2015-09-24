package org.overture.codegen.llvmgen;

import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;

public class LlvmNode
{
	private String data;
	public  ModuleRef modref;

	public LlvmNode(String data)
	{
		this.data = data;
	}

	public LlvmNode(String name, ModuleRef mod)
	{
		this(name);
		this.modref = mod;
	}

	@Override
	public String toString()
	{
		return "Name: " + data + "\n\n"
				+ (modref != null ? LLVM.PrintModuleToString(modref) : "");
	}
}
