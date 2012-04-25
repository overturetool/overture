package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlVariable extends IJmlNode
{
	abstract IJmlAccessDefinition getAccess() throws CGException;
	abstract Boolean getModel() throws CGException;
	abstract Boolean getStatickeyword() throws CGException;
	abstract Boolean getFinal() throws CGException;
	abstract IJmlType getType() throws CGException;
	abstract String getIdentifier() throws CGException;
	abstract IJmlExpression getExpression() throws CGException;
	abstract Boolean hasExpression() throws CGException;
}

