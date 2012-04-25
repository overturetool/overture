package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIfExpression extends IOmlExpression
{
	abstract IOmlExpression getIfExpression() throws CGException;
	abstract IOmlExpression getThenExpression() throws CGException;
	abstract Vector getElseifExpressionList() throws CGException;
	abstract IOmlExpression getElseExpression() throws CGException;
}

