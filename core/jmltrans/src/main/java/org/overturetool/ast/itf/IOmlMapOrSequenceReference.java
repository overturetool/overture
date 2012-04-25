package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMapOrSequenceReference extends IOmlStateDesignator
{
	abstract IOmlStateDesignator getStateDesignator() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

