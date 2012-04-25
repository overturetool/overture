package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlUnaryExpression extends IJmlExpression
{
	abstract IJmlUnaryOperator getOperator() throws CGException;
	abstract IJmlExpression getExpression() throws CGException;
}

