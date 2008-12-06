package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlActExpression extends IOmlExpression
{
	abstract Vector getNameList() throws CGException;
}

