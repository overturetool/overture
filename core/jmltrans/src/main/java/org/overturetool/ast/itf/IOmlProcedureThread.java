package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlProcedureThread extends IOmlThreadSpecification
{
	abstract IOmlStatement getStatement() throws CGException;
}

