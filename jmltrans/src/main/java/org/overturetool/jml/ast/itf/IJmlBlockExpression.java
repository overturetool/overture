package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlBlockExpression extends IJmlExpression
{
	abstract Vector getBind() throws CGException;
	abstract IJmlExpression getReturnExpr() throws CGException;
}

