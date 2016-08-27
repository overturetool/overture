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

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;

public class PatternInfo
{
	private STypeIR type;
	private SPatternIR pattern;
	private SExpIR actualValue;

	public PatternInfo(STypeIR type, SPatternIR pattern, SExpIR actualValue)
	{
		this.type = type;
		this.pattern = pattern;
		this.actualValue = actualValue;
	}

	public STypeIR getType()
	{
		return type;
	}

	public SPatternIR getPattern()
	{
		return pattern;
	}

	public SExpIR getActualValue()
	{
		return actualValue;
	}

	@Override
	public String toString()
	{
		return String.format("Type: %s\nPattern: %s\nValue: %s", type, pattern, actualValue);
	}
}
