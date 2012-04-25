package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlDefExpression extends IOmlExpression
{
	abstract Vector getPatternBindList() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

