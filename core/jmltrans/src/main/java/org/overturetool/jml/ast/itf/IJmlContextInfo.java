package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlContextInfo
{
	abstract void accept(IJmlVisitor theVisitor) throws CGException;
}

