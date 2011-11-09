package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlParameter extends IOmlNode
{
	@SuppressWarnings("rawtypes")
	abstract Vector getPatternList() throws CGException;
}

