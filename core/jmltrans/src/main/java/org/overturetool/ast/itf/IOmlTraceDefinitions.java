package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceDefinitions extends IOmlDefinitionBlock
{
	abstract Vector getTraces() throws CGException;
}

