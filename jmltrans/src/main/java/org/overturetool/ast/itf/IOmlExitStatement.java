package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExitStatement extends IOmlStatement
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract Boolean hasExpression() throws CGException;
}

