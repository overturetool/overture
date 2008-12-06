package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlIfExpression extends IJmlExpression
{
	abstract IJmlExpression getIfExpression() throws CGException;
	abstract IJmlExpression getThenExpression() throws CGException;
	abstract IJmlExpression getElseExpression() throws CGException;
}

