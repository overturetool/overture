package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlLiteralExpression extends IJmlExpression
{
	abstract IJmlLiteral getLit() throws CGException;
}

