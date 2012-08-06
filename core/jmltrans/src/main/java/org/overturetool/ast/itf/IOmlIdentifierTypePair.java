package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIdentifierTypePair extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlType getType() throws CGException;
}

