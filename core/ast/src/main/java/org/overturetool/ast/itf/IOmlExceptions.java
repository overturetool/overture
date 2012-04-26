package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExceptions extends IOmlNode
{
	@SuppressWarnings("rawtypes")
	abstract Vector getErrorList() throws CGException;
}

