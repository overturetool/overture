package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionTypeSelect extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlFunctionTypeInstantiation getFunctionTypeInstantiation() throws CGException;
}

