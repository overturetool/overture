package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlValueShape extends IOmlNode
{
	abstract IOmlPattern getPattern() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract Boolean hasType() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

