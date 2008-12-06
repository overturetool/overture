package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFieldSelect extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlName getName() throws CGException;
}

