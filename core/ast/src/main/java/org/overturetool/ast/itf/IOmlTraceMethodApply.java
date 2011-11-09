package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceMethodApply extends IOmlTraceCoreDefinition
{
	abstract String getVariableName() throws CGException;
	abstract String getMethodName() throws CGException;
	@SuppressWarnings("rawtypes")
	abstract Vector getArgs() throws CGException;
}

