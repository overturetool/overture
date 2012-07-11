package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSpecifications extends IOmlNode
{
	abstract Vector getClassList() throws CGException;
}

