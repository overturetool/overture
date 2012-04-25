package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSimpleType extends IOmlTypeShape
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract IOmlInvariant getInvariant() throws CGException;
	abstract Boolean hasInvariant() throws CGException;
}

