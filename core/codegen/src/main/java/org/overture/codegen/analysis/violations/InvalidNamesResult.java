package org.overture.codegen.analysis.violations;

import java.util.HashSet;
import java.util.Set;

public class InvalidNamesResult
{
	private Set<Violation> reservedWordViolations;
	private Set<Violation> typenameViolations;
	private Set<Violation> tempVarViolations;
	
	private String correctionPrefix;
	
	public InvalidNamesResult(Set<Violation> reservedWordViolations,
			Set<Violation> typenameViolations, Set<Violation> tempVarViolations, String correctionPrefix)
	{
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
		this.tempVarViolations = tempVarViolations;
		
		this.correctionPrefix = correctionPrefix;
	}
	
	public InvalidNamesResult()
	{
		this.reservedWordViolations = new HashSet<Violation>();
		this.typenameViolations = new HashSet<Violation>();
		this.tempVarViolations = new HashSet<Violation>();
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
	
	public boolean isEmpty()
	{
		return reservedWordViolations.isEmpty() && typenameViolations.isEmpty() && tempVarViolations.isEmpty();
	}

	public String getCorrectionPrefix()
	{
		return correctionPrefix;
	}
}
