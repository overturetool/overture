package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlAccessDefinition extends IJmlNode
{
	abstract IJmlScope getScope() throws CGException;
}

