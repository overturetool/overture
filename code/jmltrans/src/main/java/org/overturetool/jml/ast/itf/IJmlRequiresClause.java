package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlRequiresClause extends IJmlTrailers
{
	abstract IJmlExpression getRequiresExpression() throws CGException;
}

