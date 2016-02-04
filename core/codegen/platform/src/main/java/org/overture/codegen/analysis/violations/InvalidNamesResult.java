/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.analysis.violations;

import java.util.HashSet;
import java.util.Set;

public class InvalidNamesResult
{
	private Set<Violation> reservedWordViolations;
	private Set<Violation> typenameViolations;
	private Set<Violation> tempVarViolations;
	private Set<Violation> objectMethodViolations;

	private String correctionPrefix;

	public InvalidNamesResult(Set<Violation> reservedWordViolations,
			Set<Violation> typenameViolations,
			Set<Violation> tempVarViolations, Set<Violation> objectMethodViolations, String correctionPrefix)
	{
		this.reservedWordViolations = reservedWordViolations;
		this.typenameViolations = typenameViolations;
		this.tempVarViolations = tempVarViolations;
		this.objectMethodViolations = objectMethodViolations;

		this.correctionPrefix = correctionPrefix;
	}

	public InvalidNamesResult()
	{
		this.reservedWordViolations = new HashSet<>();
		this.typenameViolations = new HashSet<>();
		this.tempVarViolations = new HashSet<>();
		this.objectMethodViolations = new HashSet<>();
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
	
	public Set<Violation> getObjectMethodViolations()
	{
		return objectMethodViolations;
	}

	public boolean isEmpty()
	{
		return reservedWordViolations.isEmpty() && typenameViolations.isEmpty()
				&& tempVarViolations.isEmpty() && objectMethodViolations.isEmpty();
	}

	public String getCorrectionPrefix()
	{
		return correctionPrefix;
	}
}
