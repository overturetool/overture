package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMapEnumeration extends IOmlExpression
{
	@SuppressWarnings("rawtypes")
	abstract Vector getMapletList() throws CGException;
}

