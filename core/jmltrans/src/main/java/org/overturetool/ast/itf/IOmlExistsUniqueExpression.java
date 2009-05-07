package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExistsUniqueExpression extends IOmlExpression
{
	abstract IOmlBind getBind() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

