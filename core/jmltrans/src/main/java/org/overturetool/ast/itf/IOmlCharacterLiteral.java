package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCharacterLiteral extends IOmlLiteral
{
	abstract Character getVal() throws CGException;
}

