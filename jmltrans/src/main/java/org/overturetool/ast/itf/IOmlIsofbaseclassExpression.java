package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIsofbaseclassExpression extends IOmlExpression
{
	abstract IOmlName getName() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

