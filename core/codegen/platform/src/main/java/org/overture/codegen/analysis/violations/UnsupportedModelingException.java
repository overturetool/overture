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
