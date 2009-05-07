package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTokenExpression extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
}

