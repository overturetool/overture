package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceChoiceDefinition extends IOmlTraceDefinition
{
	@SuppressWarnings("rawtypes")
	abstract Vector getDefs() throws CGException;
}

