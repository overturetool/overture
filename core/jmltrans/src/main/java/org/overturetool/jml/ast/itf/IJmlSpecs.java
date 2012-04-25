package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSpecs extends IJmlNode
{
	abstract Vector getList() throws CGException;
}

