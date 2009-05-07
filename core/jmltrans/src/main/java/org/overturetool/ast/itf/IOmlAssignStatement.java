package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlAssignStatement extends IOmlStatement
{
	abstract IOmlStateDesignator getStateDesignator() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

