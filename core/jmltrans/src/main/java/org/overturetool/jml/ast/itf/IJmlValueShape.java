package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlValueShape extends IJmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract IJmlType getType() throws CGException;
	abstract IJmlExpression getExpression() throws CGException;
}

