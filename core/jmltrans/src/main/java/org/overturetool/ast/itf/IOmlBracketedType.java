package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlBracketedType extends IOmlType
{
	abstract IOmlType getType() throws CGException;
}

