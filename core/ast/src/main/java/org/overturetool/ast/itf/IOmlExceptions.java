package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExceptions extends IOmlNode
{
	@SuppressWarnings("unchecked")
	abstract Vector getErrorList() throws CGException;
}

