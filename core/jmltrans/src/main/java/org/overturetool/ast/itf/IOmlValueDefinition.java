package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlValueDefinition extends IOmlNode
{
	abstract IOmlAccessDefinition getAccess() throws CGException;
	abstract IOmlValueShape getShape() throws CGException;
}

