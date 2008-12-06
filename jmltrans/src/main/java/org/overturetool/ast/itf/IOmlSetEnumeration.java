package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetEnumeration extends IOmlExpression
{
	abstract Vector getExpressionList() throws CGException;
}

