package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlApplyExpression extends IJmlExpression
{
	abstract IJmlExpression getExpression() throws CGException;
	abstract Vector getExpressionList() throws CGException;
}

