package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlDclStatement extends IOmlStatement
{
	@SuppressWarnings("rawtypes")
	abstract Vector getDefinitionList() throws CGException;
}

