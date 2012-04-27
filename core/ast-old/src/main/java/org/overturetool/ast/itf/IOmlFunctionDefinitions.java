package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionDefinitions extends IOmlDefinitionBlock
{
	@SuppressWarnings("rawtypes")
	abstract Vector getFunctionList() throws CGException;
}

