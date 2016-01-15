package org.overture.codegen.tests.exec.util;

public class ProcessResult
{
	// The exit code returned by the process
	private int exitCode;
	
	// The output of the process read from the input and error streams
	private StringBuilder output;

	public ProcessResult(int exitCode, StringBuilder output)
	{
		super();
		this.exitCode = exitCode;
		this.output = output;
	}

	public int getExitCode()
	{
		return exitCode;
	}

	public StringBuilder getOutput()
	{
		return output;
	}
}
