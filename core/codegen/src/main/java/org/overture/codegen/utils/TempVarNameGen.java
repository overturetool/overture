package org.overture.codegen.utils;

public class TempVarNameGen
{
	public static final String GENERATED_TEMP_VAR_NAME_PREFIX = "temp_";
	
	private long nextVar;
	
	public TempVarNameGen()
	{
		super();
		this.nextVar = 1;
	}
	
	public String nextVarName()
	{
		return GENERATED_TEMP_VAR_NAME_PREFIX + nextVar++;
	}
}
