package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetComprehension extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract Vector getBindList() throws CGException;
	abstract IOmlExpression getGuard() throws CGException;
	abstract Boolean hasGuard() throws CGException;
}

