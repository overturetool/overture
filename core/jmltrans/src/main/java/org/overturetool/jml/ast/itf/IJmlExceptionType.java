package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlExceptionType extends IJmlNode
{
	abstract String getType() throws CGException;
}

