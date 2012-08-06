package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlExpressionsList extends IJmlPrimarySuffix
{
	abstract Vector getList() throws CGException;
}

