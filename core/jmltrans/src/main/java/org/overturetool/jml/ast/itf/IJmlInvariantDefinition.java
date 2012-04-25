package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlInvariantDefinition extends IJmlNode
{
	abstract IJmlAccessDefinition getAccess() throws CGException;
	abstract Boolean getRedundant() throws CGException;
	abstract IJmlExpression getPredicate() throws CGException;
}

