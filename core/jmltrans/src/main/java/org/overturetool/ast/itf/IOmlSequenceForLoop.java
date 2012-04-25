package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSequenceForLoop extends IOmlStatement
{
	abstract IOmlPatternBind getPatternBind() throws CGException;
	abstract Boolean getInReverse() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

