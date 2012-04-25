package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlName extends IJmlExpression
{
	abstract String getClassIdentifier() throws CGException;
	abstract Boolean hasClassIdentifier() throws CGException;
	abstract String getIdentifier() throws CGException;
}

