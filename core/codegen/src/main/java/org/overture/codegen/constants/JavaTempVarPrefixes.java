package org.overture.codegen.constants;

public class JavaTempVarPrefixes
{
	//The fields are wrapped in methods to make them easily accessible
	//to the Velocity Template Engine.
	
	public static final String SET_NAME_PREFIX = "set_";
	
	public static String getSetNamePrefix()
	{
		return SET_NAME_PREFIX;
	}
	
	public static final String ITERATOR_NAME_PREFIX = "iterator_";
	
	public static final String getIteratorNamePrefix()
	{
		return ITERATOR_NAME_PREFIX;
	}
	
	public static final String SUCCESS_VAR_NAME_PREFIX = "success_";
	
	public static String getSuccessVarNamePrefix()
	{
		return SUCCESS_VAR_NAME_PREFIX;
	}
	
	public static final String FOR_INDEX_TO_VAR_NAME_PREFIX = "toVar_";
	
	public static String getForIndexToVarNamePrefix()
	{
		return FOR_INDEX_TO_VAR_NAME_PREFIX;
	}
	
	public static final String FOR_INDEX_BY_VAR_NAME_PREFIX = "byVar_";
	
	public static String getForIndexByVarNamePrefix()
	{
		return FOR_INDEX_BY_VAR_NAME_PREFIX;
	}
	
	public static final String[] GENERATED_TEMP_NAMES = {FOR_INDEX_BY_VAR_NAME_PREFIX,
														FOR_INDEX_TO_VAR_NAME_PREFIX,
														ITERATOR_NAME_PREFIX,
														SET_NAME_PREFIX,
														SUCCESS_VAR_NAME_PREFIX};
}
