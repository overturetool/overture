package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExistsExpression extends IOmlExpression
{
	abstract Vector getBindList() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

