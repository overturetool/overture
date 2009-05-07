package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlElseIfStatement extends IOmlNode
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

