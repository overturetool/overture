package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlEqualsDefinition extends IOmlNode
{
	abstract IOmlPatternBind getPatternBind() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

