package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypeDefinition extends IOmlNode
{
	abstract IOmlAccessDefinition getAccess() throws CGException;
	abstract IOmlTypeShape getShape() throws CGException;
}

