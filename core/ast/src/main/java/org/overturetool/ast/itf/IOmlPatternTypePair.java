package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlPatternTypePair extends IOmlNode
{
	@SuppressWarnings("rawtypes")
	abstract Vector getPatternList() throws CGException;
	abstract IOmlType getType() throws CGException;
}

