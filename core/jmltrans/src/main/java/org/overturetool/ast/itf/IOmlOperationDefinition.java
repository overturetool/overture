package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlOperationDefinition extends IOmlNode
{
	abstract IOmlAccessDefinition getAccess() throws CGException;
	abstract IOmlOperationShape getShape() throws CGException;
}

