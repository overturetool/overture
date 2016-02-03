package org.overture.codegen.trans;

import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.analysis.intf.IAnalysis;

public interface ITotalTransformation extends IAnalysis
{
	public PIR getResult();
}
