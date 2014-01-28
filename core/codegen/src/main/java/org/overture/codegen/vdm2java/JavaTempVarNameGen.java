package org.overture.codegen.vdm2java;

public class JavaTempVarNameGen
{
	private static final String VAR_NAME_PREFIX = "temp_";
	private long nextVar;
	
	public JavaTempVarNameGen()
	{
		super();
		this.nextVar = 1;
	}
	
	public String nextVarName()
	{
		return VAR_NAME_PREFIX + nextVar++;
	}
}
