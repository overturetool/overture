package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlCharacterLiteral extends IJmlLiteral
{
	abstract Character getVal() throws CGException;
}

