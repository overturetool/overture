package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNumericLiteral extends IOmlLiteral
{
	abstract Long getVal() throws CGException;
}

