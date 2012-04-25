package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCyclesStatement extends IOmlStatement
{
	abstract Vector getCyclesExpression() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

