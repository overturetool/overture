package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlLambdaExpression extends IOmlExpression
{
	@SuppressWarnings("rawtypes")
	abstract Vector getTypeBindList() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

