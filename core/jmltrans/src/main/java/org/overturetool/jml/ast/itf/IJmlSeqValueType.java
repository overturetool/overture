package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSeqValueType extends IJmlType
{
	abstract IJmlType getType() throws CGException;
	abstract Long getLimit() throws CGException;
}

