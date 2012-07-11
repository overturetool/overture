package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSeqConcPattern extends IOmlPattern
{
	abstract IOmlPattern getLhsPattern() throws CGException;
	abstract IOmlPattern getRhsPattern() throws CGException;
}

