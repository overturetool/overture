package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlException extends IJmlNode
{
	abstract IJmlExceptionType getType() throws CGException;
	abstract String getIdentifier() throws CGException;
}

