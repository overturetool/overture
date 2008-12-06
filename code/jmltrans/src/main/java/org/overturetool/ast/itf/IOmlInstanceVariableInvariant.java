package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlInstanceVariableInvariant extends IOmlInstanceVariableShape
{
	abstract IOmlExpression getInvariant() throws CGException;
}

