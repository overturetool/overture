package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlInvariant extends IOmlNode
{
	abstract IOmlPattern getPattern() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

