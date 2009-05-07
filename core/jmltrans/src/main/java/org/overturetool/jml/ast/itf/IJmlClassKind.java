package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlClassKind extends IJmlNode
{
	abstract void setValue(Long val) throws CGException;
	abstract Long getValue() throws CGException;
	abstract String getStringValue() throws CGException;
}

