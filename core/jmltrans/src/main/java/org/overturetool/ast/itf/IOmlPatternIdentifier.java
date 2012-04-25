package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlPatternIdentifier extends IOmlPattern
{
	abstract String getIdentifier() throws CGException;
}

