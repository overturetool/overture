package org.overturetool.test.framework.examples;

import java.util.Set;


public class Result<R>
{
	public final R result;
	public final Set<IMessage> warnings;
	public final Set<IMessage> errors;

	public Result(R result, Set<IMessage> warnings, Set<IMessage> errors)
	{
		this.result = result;
		this.warnings = warnings;
		this.errors = errors;
	}
}
