package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlNameId extends IJmlPrimaryExpressionOption
{
	abstract String getName() throws CGException;
}

