package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlFloatLiteral extends IJmlLiteral
{
	abstract Double getVal() throws CGException;
}

