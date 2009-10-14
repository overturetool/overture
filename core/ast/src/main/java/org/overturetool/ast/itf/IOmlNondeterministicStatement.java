package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNondeterministicStatement extends IOmlStatement
{
	@SuppressWarnings("unchecked")
	abstract Vector getStatementList() throws CGException;
}

