package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTrapDefinition extends IOmlNode
{
	abstract IOmlPatternBind getPatternBind() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

