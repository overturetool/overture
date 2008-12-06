package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypeName extends IOmlType
{
	abstract IOmlName getName() throws CGException;
}

