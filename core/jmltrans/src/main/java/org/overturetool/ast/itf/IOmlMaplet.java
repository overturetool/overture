package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMaplet extends IOmlNode
{
	abstract IOmlExpression getDomExpression() throws CGException;
	abstract IOmlExpression getRngExpression() throws CGException;
}

