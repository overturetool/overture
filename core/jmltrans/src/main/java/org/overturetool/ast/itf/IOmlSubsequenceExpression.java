package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSubsequenceExpression extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlExpression getLower() throws CGException;
	abstract IOmlExpression getUpper() throws CGException;
}

