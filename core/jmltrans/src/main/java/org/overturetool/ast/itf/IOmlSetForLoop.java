package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetForLoop extends IOmlStatement
{
	abstract IOmlPattern getPattern() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

