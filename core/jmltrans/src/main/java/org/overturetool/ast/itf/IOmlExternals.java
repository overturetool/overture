package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExternals extends IOmlNode
{
	abstract Vector getExtList() throws CGException;
}

