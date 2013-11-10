package org.overture.codegen.utils;

import java.util.List;

public class InvalidNamesException extends Exception
{
	private static final long serialVersionUID = -8370037325877588512L;
	
	private List<NameViolation> reservedWordViolations;
	private List<NameViolation> typenameViolations;
	
	public InvalidNamesException(String message, List<NameViolation> reservedWordViolations,
			List<NameViolation> typenameViolations)
	{
		super(message);
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
	}

	public List<NameViolation> getReservedWordViolations()
	{
		return reservedWordViolations;
	}

	public List<NameViolation> getTypenameViolations()
	{
		return typenameViolations;
	}
}
