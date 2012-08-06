package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlStateDesignatorName extends IOmlStateDesignator
{
	abstract IOmlName getName() throws CGException;
}

