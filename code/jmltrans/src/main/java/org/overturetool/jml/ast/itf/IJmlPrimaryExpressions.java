package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlPrimaryExpressions extends IJmlNode
{
	abstract IJmlPrimaryExpression getPrimary() throws CGException;
	abstract IJmlPrimarySuffix getSuffix() throws CGException;
	abstract Boolean hasSuffix() throws CGException;
}

