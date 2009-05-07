package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSpecifications extends IJmlNode
{
	abstract Vector getClassList() throws CGException;
}

