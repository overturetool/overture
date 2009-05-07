package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlParameter extends IJmlNode
{
	abstract IJmlType getType() throws CGException;
	abstract String getIdentifier() throws CGException;
}

