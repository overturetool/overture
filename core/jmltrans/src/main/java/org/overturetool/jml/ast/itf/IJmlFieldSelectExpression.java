package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlFieldSelectExpression extends IJmlExpression
{
	abstract IJmlExpression getExpression() throws CGException;
	abstract IJmlName getName() throws CGException;
}

