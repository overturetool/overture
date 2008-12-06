package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTrapStatement extends IOmlStatement
{
	abstract IOmlPatternBind getPatternBind() throws CGException;
	abstract IOmlStatement getWithPart() throws CGException;
	abstract IOmlStatement getInPart() throws CGException;
}

