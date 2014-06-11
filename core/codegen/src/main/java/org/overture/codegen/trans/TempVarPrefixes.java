package org.overture.codegen.trans;

public class TempVarPrefixes
{
	public String getSetNamePrefix()
	{
		return "set_";
	}
	
	public final String getIteratorNamePrefix()
	{
		return "iterator_";
	}
	
	public String getSuccessVarNamePrefix()
	{
		return "success_";
	}
	
	public String getForIndexToVarNamePrefix()
	{
		return "toVar_";
	}
	
	public String getForIndexByVarNamePrefix()
	{
		return "byVar_";
	}
	
	public final String[] GENERATED_TEMP_NAMES = {getForIndexByVarNamePrefix(),
														getForIndexToVarNamePrefix(),
														getIteratorNamePrefix(),
														getSetNamePrefix(),
														getSuccessVarNamePrefix()};
}
