package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionBody extends IOmlNode
{
	abstract IOmlExpression getFunctionBody() throws CGException;
	abstract Boolean hasFunctionBody() throws CGException;
	abstract Boolean getNotYetSpecified() throws CGException;
	abstract Boolean getSubclassResponsibility() throws CGException;
}

