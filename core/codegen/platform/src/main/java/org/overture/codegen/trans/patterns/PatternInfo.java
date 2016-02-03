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
package org.overture.codegen.trans.patterns;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SPatternCG;
import org.overture.codegen.ir.STypeCG;

public class PatternInfo
{
	private STypeCG type;
	private SPatternCG pattern;
	private SExpCG actualValue;

	public PatternInfo(STypeCG type, SPatternCG pattern, SExpCG actualValue)
	{
		this.type = type;
		this.pattern = pattern;
		this.actualValue = actualValue;
	}

	public STypeCG getType()
	{
		return type;
	}

	public SPatternCG getPattern()
	{
		return pattern;
	}

	public SExpCG getActualValue()
	{
		return actualValue;
	}
	
	@Override
	public String toString()
	{
		return String.format("Type: %s\nPattern: %s\nValue: %s", type, pattern, actualValue);
	}
}
