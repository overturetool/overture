package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypeVariable extends IOmlType
{
	abstract String getIdentifier() throws CGException;
}

