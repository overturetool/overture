package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlApplyExpression extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getExpressionList() throws CGException;
}

