package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSporadicThread extends IOmlThreadSpecification
{
	@SuppressWarnings("rawtypes")
	abstract Vector getArgs() throws CGException;
	abstract IOmlName getName() throws CGException;
}

