package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlBlockStatement extends IOmlStatement
{
	@SuppressWarnings("rawtypes")
	abstract Vector getDclStatementList() throws CGException;
	@SuppressWarnings("rawtypes")
	abstract Vector getStatementList() throws CGException;
}

