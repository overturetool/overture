package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionDefinitions extends IOmlDefinitionBlock
{
	abstract Vector getFunctionList() throws CGException;
}

