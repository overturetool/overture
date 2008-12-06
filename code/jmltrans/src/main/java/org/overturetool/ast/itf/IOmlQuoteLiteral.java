package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlQuoteLiteral extends IOmlLiteral
{
	abstract String getVal() throws CGException;
}

