package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlObjectDesignatorExpression extends IOmlObjectDesignator
{
	abstract IOmlExpression getExpression() throws CGException;
}

