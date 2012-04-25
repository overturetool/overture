package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlEnsuresClause extends IJmlTrailers
{
	abstract IJmlExpression getEnsuresExpression() throws CGException;
}

