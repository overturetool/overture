package org.overture.codegen.vdm2java;

public class JavaTempVarNameGen
{
	private long nextVar;
	
	public JavaTempVarNameGen()
	{
		super();
		this.nextVar = 1;
	}
	
	public String nextVarName()
	{
		return JavaCodeGen.GENERATED_TEMP_VAR_NAME_PREFIX + nextVar++;
	}
}
