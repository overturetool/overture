package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMatchValue extends IOmlPattern
{
	abstract IOmlExpression getExpression() throws CGException;
}

