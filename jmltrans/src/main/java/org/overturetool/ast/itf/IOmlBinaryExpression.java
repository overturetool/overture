package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlBinaryExpression extends IOmlExpression
{
	abstract IOmlExpression getLhsExpression() throws CGException;
	abstract IOmlBinaryOperator getOperator() throws CGException;
	abstract IOmlExpression getRhsExpression() throws CGException;
}

