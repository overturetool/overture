package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlAccessDefinition extends IOmlNode
{
	abstract Boolean getAsyncAccess() throws CGException;
	abstract Boolean getStaticAccess() throws CGException;
	abstract IOmlScope getScope() throws CGException;
}

