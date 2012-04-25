package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlBracketedExpression extends IJmlExpression
{
	abstract IJmlExpression getExpression() throws CGException;
}

