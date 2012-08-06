package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionDefinition extends IOmlNode
{
	abstract IOmlAccessDefinition getAccess() throws CGException;
	abstract IOmlFunctionShape getShape() throws CGException;
}

