package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlInstanceOfExpression extends IJmlExpression
{
	abstract IJmlType getType() throws CGException;
	abstract IJmlExpression getExpression() throws CGException;
}

