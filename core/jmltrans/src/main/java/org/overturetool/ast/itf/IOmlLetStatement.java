package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlLetStatement extends IOmlStatement
{
	abstract Vector getDefinitionList() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

