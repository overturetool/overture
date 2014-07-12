package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;

public class PatternInfo
{
	private STypeCG type;
	private SPatternCG pattern;
	
	public PatternInfo(STypeCG type ,SPatternCG pattern)
	{
		this.type = type;
		this.pattern = pattern;
	}

	public STypeCG getType()
	{
		return type;
	}
	
	public SPatternCG getPattern()
	{
		return pattern;
	}
}