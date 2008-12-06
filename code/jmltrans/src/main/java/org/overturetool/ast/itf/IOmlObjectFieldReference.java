package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlObjectFieldReference extends IOmlObjectDesignator
{
	abstract IOmlObjectDesignator getObjectDesignator() throws CGException;
	abstract IOmlName getName() throws CGException;
}

