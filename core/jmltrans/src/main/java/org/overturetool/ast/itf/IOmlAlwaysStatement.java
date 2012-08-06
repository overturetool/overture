package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlAlwaysStatement extends IOmlStatement
{
	abstract IOmlStatement getAlwaysPart() throws CGException;
	abstract IOmlStatement getInPart() throws CGException;
}

