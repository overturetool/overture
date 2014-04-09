package org.overture.codegen.analysis.violations;

import java.util.Set;

public class UnsupportedModelingException extends Exception
{
	private static final long serialVersionUID = -2702857192000085876L;
	
	private Set<Violation> violations;

	public UnsupportedModelingException(String message,
			Set<Violation> violations)
	{
		super(message);
		this.violations = violations;
	}

	public Set<Violation> getViolations()
	{
		return violations;
	}
}
