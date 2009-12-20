package org.overture.ide.vdmsl.debug.core.interpreter;

import org.overture.ide.debug.interpreter.VdmjVMInterpreterRunner;


/*
 * Overrides default implementation by changing the expression to VDMSL stype
 */
public class VdmSlVdmjVMInterpreterRunner extends VdmjVMInterpreterRunner {
	@Override
	protected String buildLaunchExpression(String module, String debugOperation)
	{
		// TODO Auto-generated method stub
		return module+"`"+ debugOperation;
	}
}

