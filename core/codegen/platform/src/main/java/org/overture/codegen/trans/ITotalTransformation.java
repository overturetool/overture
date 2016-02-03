package org.overture.codegen.trans;

import org.overture.codegen.ir.PCG;
import org.overture.codegen.ir.analysis.intf.IAnalysis;

public interface ITotalTransformation extends IAnalysis
{
	public PCG getResult();
}
