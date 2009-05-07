package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlReturnStatement extends IOmlStatement
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract Boolean hasExpression() throws CGException;
}

