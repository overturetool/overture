package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlBooleanLiteral extends IOmlLiteral
{
	abstract Boolean getVal() throws CGException;
}

