package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceSequenceDefinition extends IOmlTraceDefinition
{
	@SuppressWarnings("unchecked")
	abstract Vector getDefs() throws CGException;
}

