package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSymbolicLiteralPattern extends IOmlPattern
{
	abstract IOmlLiteral getLiteral() throws CGException;
}

