package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlDurationStatement extends IOmlStatement
{
	@SuppressWarnings("rawtypes")
	abstract Vector getDurationExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

