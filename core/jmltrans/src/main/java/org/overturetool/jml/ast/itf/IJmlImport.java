package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlImport extends IJmlNode
{
	abstract String getImport() throws CGException;
}

