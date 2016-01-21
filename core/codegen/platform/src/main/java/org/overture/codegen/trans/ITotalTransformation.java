package org.overture.codegen.trans;

import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.analysis.intf.IAnalysis;

public interface ITotalTransformation extends IAnalysis
{
	public PCG getResult();
}
