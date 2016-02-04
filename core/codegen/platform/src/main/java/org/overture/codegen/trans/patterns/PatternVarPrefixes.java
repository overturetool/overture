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

import java.util.HashMap;
import java.util.Map;

import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.patterns.ABoolPatternIR;
import org.overture.codegen.ir.patterns.ACharPatternIR;
import org.overture.codegen.ir.patterns.AIgnorePatternIR;
import org.overture.codegen.ir.patterns.AIntPatternIR;
import org.overture.codegen.ir.patterns.ANullPatternIR;
import org.overture.codegen.ir.patterns.AQuotePatternIR;
import org.overture.codegen.ir.patterns.ARealPatternIR;
import org.overture.codegen.ir.patterns.ARecordPatternIR;
import org.overture.codegen.ir.patterns.AStringPatternIR;
import org.overture.codegen.ir.patterns.ATuplePatternIR;

public class PatternVarPrefixes
{
	private Map<Class<? extends SPatternIR>, String> patternNamePrefixes;

	public PatternVarPrefixes()
	{
		setupNameLookup();
	}

	private void setupNameLookup()
	{
		this.patternNamePrefixes = new HashMap<Class<? extends SPatternIR>, String>();

		this.patternNamePrefixes.put(AIgnorePatternIR.class, getIgnorePatternPrefix());
		this.patternNamePrefixes.put(ABoolPatternIR.class, getBoolPatternPrefix());
		this.patternNamePrefixes.put(ACharPatternIR.class, getCharPatternPrefix());
		this.patternNamePrefixes.put(AIntPatternIR.class, getIntPatternPrefix());
		this.patternNamePrefixes.put(ANullPatternIR.class, getNullPatternPrefix());
		this.patternNamePrefixes.put(AQuotePatternIR.class, getQuotePatternPrefix());
		this.patternNamePrefixes.put(ARealPatternIR.class, getRealPatternPrefix());
		this.patternNamePrefixes.put(AStringPatternIR.class, getStringPatternPrefix());
		this.patternNamePrefixes.put(ATuplePatternIR.class, getTuplePatternPrefix());
		this.patternNamePrefixes.put(ARecordPatternIR.class, getRecordPatternPrefix());
	}

	public String getName(Class<? extends SPatternIR> patternClass)
	{
		return patternNamePrefixes.get(patternClass);
	}

	public String getIgnorePatternPrefix()
	{
		return "ignorePattern_";
	}

	public String getBoolPatternPrefix()
	{
		return "boolPattern_";
	}

	public String getCharPatternPrefix()
	{
		return "charPattern_";
	}

	public String getIntPatternPrefix()
	{
		return "intPattern_";
	}

	public String getNullPatternPrefix()
	{
		return "nullPattern_";
	}

	public String getQuotePatternPrefix()
	{
		return "quotePattern_";
	}

	public String getRealPatternPrefix()
	{
		return "realPattern_";
	}

	public String getStringPatternPrefix()
	{
		return "stringPattern_";
	}

	public String getTuplePatternPrefix()
	{
		return "tuplePattern_";
	}

	public String getRecordPatternPrefix()
	{
		return "recordPattern_";
	}

	public String getMatchFailedMessage(SPatternIR pattern)
	{
		return patternToString(pattern) + " pattern match failed";
	}

	private String patternToString(SPatternIR pattern)
	{
		if (pattern instanceof AIgnorePatternIR)
		{
			return "Ignore";
		} else if (pattern instanceof ABoolPatternIR)
		{
			return "Bool";
		} else if (pattern instanceof ACharPatternIR)
		{
			return "Char";
		} else if (pattern instanceof AIntPatternIR)
		{
			return "Integer";
		} else if (pattern instanceof ANullPatternIR)
		{
			return "Nil";
		} else if (pattern instanceof AQuotePatternIR)
		{
			return "Quote";
		} else if (pattern instanceof ARealPatternIR)
		{
			return "Real";
		} else if (pattern instanceof AStringPatternIR)
		{
			return "String";
		} else if (pattern instanceof ATuplePatternIR)
		{
			return "Tuple";
		} else if (pattern instanceof ARecordPatternIR)
		{
			return "Record";
		} else
		{
			return null;
		}
	}
}
