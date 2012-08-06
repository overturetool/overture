package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlBinaryExpression extends IJmlExpression
{
	abstract IJmlExpression getLhsExpression() throws CGException;
	abstract IJmlBinaryOperator getOperator() throws CGException;
	abstract IJmlExpression getRhsExpression() throws CGException;
}

