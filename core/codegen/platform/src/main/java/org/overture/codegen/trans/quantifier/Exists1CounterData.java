package org.overture.codegen.trans.quantifier;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.STypeCG;

public class Exists1CounterData
{
	private STypeCG type;
	private SExpCG exp;

	public Exists1CounterData(STypeCG type, SExpCG exp)
	{
		super();
		this.type = type;
		this.exp = exp;
	}
	
	public STypeCG getType()
	{
		return type;
	}
	
	public SExpCG getExp()
	{
		return exp;
	}
}
