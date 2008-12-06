package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlRecordModifier extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

