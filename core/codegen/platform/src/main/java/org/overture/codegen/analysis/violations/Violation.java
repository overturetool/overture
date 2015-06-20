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

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.codegen.assistant.LocationAssistantCG;

public class Violation implements Comparable<Violation>
{
	private ILexLocation location;
	private String description;
	private LocationAssistantCG locationAssistant;

	public Violation(String description, ILexLocation location,
			LocationAssistantCG locationAssistant)
	{
		super();
		this.description = description;
		this.location = location;
		this.locationAssistant = locationAssistant;
	}

	public String getDescripton()
	{
		return description;
	}

	public ILexLocation getLocation()
	{
		return location;
	}

	@Override
	public String toString()
	{
		return "[Violation in module " + location.getModule() + ": '"
				+ description + "'. Location: line " + location.getStartLine()
				+ " at position: " + location.getStartPos() + " in "
				+ location.getFile().getName() + "]";
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Violation))
		{
			return false;
		}

		Violation other = (Violation) obj;

		return this.description.equals(other.description)
				&& this.location.equals(other.location);
	}

	@Override
	public int compareTo(Violation other)
	{
		return locationAssistant.compareLocations(this.location, other.location);
	}
}
