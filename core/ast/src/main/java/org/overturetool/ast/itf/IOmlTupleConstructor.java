package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTupleConstructor extends IOmlExpression
{
	@SuppressWarnings("unchecked")
	abstract Vector getExpressionList() throws CGException;
}

