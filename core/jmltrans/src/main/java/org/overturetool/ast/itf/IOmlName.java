package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlName extends IOmlExpression
{
	abstract String getClassIdentifier() throws CGException;
	abstract Boolean hasClassIdentifier() throws CGException;
	abstract String getIdentifier() throws CGException;
}

