package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlPeriodicThread extends IOmlThreadSpecification
{
	abstract Vector getArgs() throws CGException;
	abstract IOmlName getName() throws CGException;
}

