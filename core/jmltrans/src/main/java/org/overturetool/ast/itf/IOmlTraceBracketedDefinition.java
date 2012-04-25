package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceBracketedDefinition extends IOmlTraceCoreDefinition
{
	abstract IOmlTraceDefinition getDefinition() throws CGException;
}

