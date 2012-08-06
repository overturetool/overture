package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlBlockStatement extends IOmlStatement
{
	abstract Vector getDclStatementList() throws CGException;
	abstract Vector getStatementList() throws CGException;
}

