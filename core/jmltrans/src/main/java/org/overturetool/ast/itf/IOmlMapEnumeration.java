package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMapEnumeration extends IOmlExpression
{
	abstract Vector getMapletList() throws CGException;
}

