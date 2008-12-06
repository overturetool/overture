package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceLetBinding extends IOmlTraceBinding
{
	abstract Vector getDefinitionList() throws CGException;
}

