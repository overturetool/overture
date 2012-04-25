package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSpecifications extends IOmlNode
{
	@SuppressWarnings("rawtypes")
	abstract Vector getClassList() throws CGException;
}

