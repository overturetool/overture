package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlLetBeExpression extends IOmlExpression
{
	abstract IOmlBind getBind() throws CGException;
	abstract IOmlExpression getBest() throws CGException;
	abstract Boolean hasBest() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

