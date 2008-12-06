package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlOldName extends IOmlExpression
{
	abstract String getIdentifier() throws CGException;
}

