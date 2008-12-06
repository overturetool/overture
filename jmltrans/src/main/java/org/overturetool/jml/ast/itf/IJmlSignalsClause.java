package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSignalsClause extends IJmlTrailers
{
	abstract IJmlException getException() throws CGException;
	abstract IJmlExpression getPredicate() throws CGException;
}

