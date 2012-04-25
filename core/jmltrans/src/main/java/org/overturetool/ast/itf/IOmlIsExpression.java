package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIsExpression extends IOmlExpression
{
	abstract IOmlType getType() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

