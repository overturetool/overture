package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlAssignmentDefinition extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
	abstract Boolean hasExpression() throws CGException;
}

