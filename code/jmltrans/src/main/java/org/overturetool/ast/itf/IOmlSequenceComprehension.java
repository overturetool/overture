package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSequenceComprehension extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlSetBind getSetBind() throws CGException;
	abstract IOmlExpression getGuard() throws CGException;
	abstract Boolean hasGuard() throws CGException;
}

