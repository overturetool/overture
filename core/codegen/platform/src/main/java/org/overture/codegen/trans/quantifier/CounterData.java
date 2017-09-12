package org.overture.codegen.trans.quantifier;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;

public class CounterData
{
	private STypeIR type;
	private SExpIR exp;

	public CounterData(STypeIR type, SExpIR exp)
	{
		super();
		this.type = type;
		this.exp = exp;
	}

	public STypeIR getType()
	{
		return type;
	}

	public SExpIR getExp()
	{
		return exp;
	}
}
