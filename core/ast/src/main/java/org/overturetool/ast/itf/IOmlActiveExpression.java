package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlActiveExpression extends IOmlExpression
{
	@SuppressWarnings("unchecked")
	abstract Vector getNameList() throws CGException;
}

