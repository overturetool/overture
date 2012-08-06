package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlStartStatement extends IOmlStatement
{
	abstract IOmlExpression getExpression() throws CGException;
}

