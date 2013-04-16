package org.overture.ide.debug.utils;

import org.overture.ast.analysis.AnalysisException;

public class ExecutableAnalysisException extends AnalysisException
{
	private static final long serialVersionUID = 3652000541884950337L;
	
	private boolean executable = false;

	public ExecutableAnalysisException(boolean executable)
	{
		super();
		this.executable = executable;
	}

	public boolean isExecutable()
	{
		return executable;
	}
}
