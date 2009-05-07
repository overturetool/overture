package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlClassName extends IJmlType
{
	abstract IJmlName getId() throws CGException;
}

