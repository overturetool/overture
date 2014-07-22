package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;

public class PatternInfo
{
	private STypeCG type;
	private SPatternCG pattern;
	private SExpCG actualValue;
	
	public PatternInfo(STypeCG type ,SPatternCG pattern, SExpCG actualValue)
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
}