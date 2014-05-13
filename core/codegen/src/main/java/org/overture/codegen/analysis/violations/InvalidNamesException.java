package org.overture.codegen.analysis.violations;

import java.util.Set;

public class InvalidNamesException extends Exception
{
	private static final long serialVersionUID = -8370037325877588512L;
	
	private Set<Violation> reservedWordViolations;
	private Set<Violation> typenameViolations;
	private Set<Violation> tempVarViolations;
	
	public InvalidNamesException(String message, Set<Violation> reservedWordViolations,
			Set<Violation> typenameViolations, Set<Violation> tempVarViolations)
	{
		super(message);
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
		this.tempVarViolations = tempVarViolations;
	}

	public Set<Violation> getReservedWordViolations()
	{
		return reservedWordViolations;
	}

	public Set<Violation> getTypenameViolations()
	{
		return typenameViolations;
	}

	public Set<Violation> getTempVarViolations()
	{
		return tempVarViolations;
	}
}
