package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlUnaryExpression extends IOmlExpression
{
	abstract IOmlUnaryOperator getOperator() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

