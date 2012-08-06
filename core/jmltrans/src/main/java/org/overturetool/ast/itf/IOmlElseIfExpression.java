package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlElseIfExpression extends IOmlNode
{
	abstract IOmlExpression getElseifExpression() throws CGException;
	abstract IOmlExpression getThenExpression() throws CGException;
}

