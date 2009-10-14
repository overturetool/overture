package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNewExpression extends IOmlExpression
{
	abstract IOmlName getName() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getGenericTypes() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getExpressionList() throws CGException;
}

