package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetRangeExpression extends IOmlExpression
{
	abstract IOmlExpression getLower() throws CGException;
	abstract IOmlExpression getUpper() throws CGException;
}

