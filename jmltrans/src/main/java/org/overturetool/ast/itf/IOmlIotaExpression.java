package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIotaExpression extends IOmlExpression
{
	abstract IOmlBind getBind() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

