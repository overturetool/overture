package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSetValueType extends IJmlType
{
	abstract IJmlType getType() throws CGException;
}

