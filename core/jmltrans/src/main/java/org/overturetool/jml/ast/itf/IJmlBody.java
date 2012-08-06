package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlBody extends IJmlNode
{
	abstract String getBody() throws CGException;
}

