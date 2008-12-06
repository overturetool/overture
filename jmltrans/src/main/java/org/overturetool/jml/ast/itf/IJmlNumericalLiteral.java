package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlNumericalLiteral extends IJmlLiteral
{
	abstract Long getVal() throws CGException;
}

