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

import org.overture.codegen.ir.SPatternCG;
import org.overture.codegen.ir.patterns.ABoolPatternCG;
import org.overture.codegen.ir.patterns.ACharPatternCG;
import org.overture.codegen.ir.patterns.AIgnorePatternCG;
import org.overture.codegen.ir.patterns.AIntPatternCG;
import org.overture.codegen.ir.patterns.ANullPatternCG;
import org.overture.codegen.ir.patterns.AQuotePatternCG;
import org.overture.codegen.ir.patterns.ARealPatternCG;
import org.overture.codegen.ir.patterns.ARecordPatternCG;
import org.overture.codegen.ir.patterns.AStringPatternCG;
import org.overture.codegen.ir.patterns.ATuplePatternCG;

public class PatternVarPrefixes
{
	private Map<Class<? extends SPatternCG>, String> patternNamePrefixes;

	public PatternVarPrefixes()
	{
		setupNameLookup();
	}

	private void setupNameLookup()
	{
		this.patternNamePrefixes = new HashMap<Class<? extends SPatternCG>, String>();

		this.patternNamePrefixes.put(AIgnorePatternCG.class, getIgnorePatternPrefix());
		this.patternNamePrefixes.put(ABoolPatternCG.class, getBoolPatternPrefix());
		this.patternNamePrefixes.put(ACharPatternCG.class, getCharPatternPrefix());
		this.patternNamePrefixes.put(AIntPatternCG.class, getIntPatternPrefix());
		this.patternNamePrefixes.put(ANullPatternCG.class, getNullPatternPrefix());
		this.patternNamePrefixes.put(AQuotePatternCG.class, getQuotePatternPrefix());
		this.patternNamePrefixes.put(ARealPatternCG.class, getRealPatternPrefix());
		this.patternNamePrefixes.put(AStringPatternCG.class, getStringPatternPrefix());
		this.patternNamePrefixes.put(ATuplePatternCG.class, getTuplePatternPrefix());
		this.patternNamePrefixes.put(ARecordPatternCG.class, getRecordPatternPrefix());
	}

	public String getName(Class<? extends SPatternCG> patternClass)
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

	public String getMatchFailedMessage(SPatternCG pattern)
	{
		return patternToString(pattern) + " pattern match failed";
	}

	private String patternToString(SPatternCG pattern)
	{
		if (pattern instanceof AIgnorePatternCG)
		{
			return "Ignore";
		} else if (pattern instanceof ABoolPatternCG)
		{
			return "Bool";
		} else if (pattern instanceof ACharPatternCG)
		{
			return "Char";
		} else if (pattern instanceof AIntPatternCG)
		{
			return "Integer";
		} else if (pattern instanceof ANullPatternCG)
		{
			return "Nil";
		} else if (pattern instanceof AQuotePatternCG)
		{
			return "Quote";
		} else if (pattern instanceof ARealPatternCG)
		{
			return "Real";
		} else if (pattern instanceof AStringPatternCG)
		{
			return "String";
		} else if (pattern instanceof ATuplePatternCG)
		{
			return "Tuple";
		} else if (pattern instanceof ARecordPatternCG)
		{
			return "Record";
		} else
		{
			return null;
		}
	}
}
