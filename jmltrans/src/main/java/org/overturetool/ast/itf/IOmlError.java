package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlError extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlExpression getLhs() throws CGException;
	abstract IOmlExpression getRhs() throws CGException;
}

