package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlPrimaryExpressionLiteral extends IJmlPrimaryExpression
{
	abstract IJmlLiteral getLit() throws CGException;
}

