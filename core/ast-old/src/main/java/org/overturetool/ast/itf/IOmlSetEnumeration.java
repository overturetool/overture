package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetEnumeration extends IOmlExpression
{
	@SuppressWarnings("rawtypes")
	abstract Vector getExpressionList() throws CGException;
}

