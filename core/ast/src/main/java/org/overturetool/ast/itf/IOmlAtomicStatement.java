package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlAtomicStatement extends IOmlStatement
{
	@SuppressWarnings("unchecked")
	abstract Vector getAssignmentList() throws CGException;
}

