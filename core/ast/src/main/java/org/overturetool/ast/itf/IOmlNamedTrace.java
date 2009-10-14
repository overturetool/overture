package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNamedTrace extends IOmlNode
{
	@SuppressWarnings("unchecked")
	abstract Vector getName() throws CGException;
	abstract IOmlTraceDefinition getDefs() throws CGException;
}

