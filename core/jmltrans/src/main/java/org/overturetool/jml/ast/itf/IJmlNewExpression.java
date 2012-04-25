package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlNewExpression extends IJmlExpression
{
	abstract IJmlType getType() throws CGException;
	abstract Vector getExpressionList() throws CGException;
}

