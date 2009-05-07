package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlRecursiveTrapStatement extends IOmlStatement
{
	abstract Vector getTrapList() throws CGException;
	abstract IOmlStatement getInPart() throws CGException;
}

