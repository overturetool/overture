package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFieldReference extends IOmlStateDesignator
{
	abstract IOmlStateDesignator getStateDesignator() throws CGException;
	abstract String getIdentifier() throws CGException;
}

