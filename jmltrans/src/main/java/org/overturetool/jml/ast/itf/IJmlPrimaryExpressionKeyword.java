package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlPrimaryExpressionKeyword extends IJmlPrimaryExpression
{
	abstract IJmlPrimaryExpressionOption getKeyword() throws CGException;
}

