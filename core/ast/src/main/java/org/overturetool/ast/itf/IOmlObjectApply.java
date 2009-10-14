package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlObjectApply extends IOmlObjectDesignator
{
	abstract IOmlObjectDesignator getObjectDesignator() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getExpressionList() throws CGException;
}

