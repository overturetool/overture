package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlPermissionPredicate extends IOmlSyncPredicate
{
	abstract IOmlName getName() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

