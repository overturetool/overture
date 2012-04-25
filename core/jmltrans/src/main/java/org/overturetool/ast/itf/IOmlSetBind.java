package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSetBind extends IOmlBind
{
	abstract Vector getPattern() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

