package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypeBind extends IOmlBind
{
	abstract Vector getPattern() throws CGException;
	abstract IOmlType getType() throws CGException;
}

