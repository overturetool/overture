package org.overture.codegen.utils;

import java.util.List;

public class InvalidNamesException extends Exception
{
	private static final long serialVersionUID = -8370037325877588512L;
	
	private List<Violation> reservedWordViolations;
	private List<Violation> typenameViolations;
	
	public InvalidNamesException(String message, List<Violation> reservedWordViolations,
			List<Violation> typenameViolations)
	{
		super(message);
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
	}

	public List<Violation> getReservedWordViolations()
	{
		return reservedWordViolations;
	}

	public List<Violation> getTypenameViolations()
	{
		return typenameViolations;
	}
}
