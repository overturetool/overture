package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlOperationBody extends IOmlNode
{
	abstract IOmlStatement getStatement() throws CGException;
	abstract Boolean hasStatement() throws CGException;
	abstract Boolean getNotYetSpecified() throws CGException;
	abstract Boolean getSubclassResponsibility() throws CGException;
}

