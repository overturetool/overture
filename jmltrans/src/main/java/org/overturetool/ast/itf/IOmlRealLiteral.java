package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlRealLiteral extends IOmlLiteral
{
	abstract Double getVal() throws CGException;
}

