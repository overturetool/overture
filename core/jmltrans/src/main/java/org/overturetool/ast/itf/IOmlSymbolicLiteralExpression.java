package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSymbolicLiteralExpression extends IOmlExpression
{
	abstract IOmlLiteral getLiteral() throws CGException;
}

