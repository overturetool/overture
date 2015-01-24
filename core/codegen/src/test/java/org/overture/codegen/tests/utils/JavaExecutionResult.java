package org.overture.codegen.tests.utils;

public class JavaExecutionResult
{
	private String processOutput;
	private Object executionResult;
	
	public JavaExecutionResult(String processOutput, Object executionResult)
	{
		this.processOutput = processOutput;
		this.executionResult = executionResult;
	}
	
	public String getProcessOutput()
	{
		return processOutput;
	}
	public Object getExecutionResult()
	{
		return executionResult;
	}
}
