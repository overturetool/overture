package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNewExpression extends IOmlExpression
{
	abstract IOmlName getName() throws CGException;
	abstract Vector getGenericTypes() throws CGException;
	abstract Vector getExpressionList() throws CGException;
}

