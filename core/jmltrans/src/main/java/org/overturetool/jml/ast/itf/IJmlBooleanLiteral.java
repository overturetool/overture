package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlBooleanLiteral extends IJmlLiteral
{
	abstract Boolean getVal() throws CGException;
}

