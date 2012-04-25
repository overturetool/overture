package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIndexForLoop extends IOmlStatement
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlExpression getInitExpression() throws CGException;
	abstract IOmlExpression getLimitExpression() throws CGException;
	abstract IOmlExpression getByExpression() throws CGException;
	abstract Boolean hasByExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

