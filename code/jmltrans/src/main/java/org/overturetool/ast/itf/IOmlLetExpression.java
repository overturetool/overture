package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlLetExpression extends IOmlExpression
{
	abstract Vector getDefinitionList() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
}

