package org.overture.codegen.utils;

import java.util.Set;

public class InvalidNamesException extends Exception
{
	private static final long serialVersionUID = -8370037325877588512L;
	
	private Set<Violation> reservedWordViolations;
	private Set<Violation> typenameViolations;
	
	public InvalidNamesException(String message, Set<Violation> reservedWordViolations,
			Set<Violation> typenameViolations)
	{
		super(message);
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
	}

	public Set<Violation> getReservedWordViolations()
	{
		return reservedWordViolations;
	}

	public Set<Violation> getTypenameViolations()
	{
		return typenameViolations;
	}
}
