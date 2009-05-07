package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCasesStatementAlternative extends IOmlNode
{
	abstract Vector getPatternList() throws CGException;
	abstract IOmlStatement getStatement() throws CGException;
}

