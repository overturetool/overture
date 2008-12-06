package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMapComprehension extends IOmlExpression
{
	abstract IOmlMaplet getExpression() throws CGException;
	abstract Vector getBindList() throws CGException;
	abstract IOmlExpression getGuard() throws CGException;
	abstract Boolean hasGuard() throws CGException;
}

