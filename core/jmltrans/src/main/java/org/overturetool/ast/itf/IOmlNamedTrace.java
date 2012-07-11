package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNamedTrace extends IOmlNode
{
	abstract String getName() throws CGException;
	abstract IOmlTraceDefinition getDefs() throws CGException;
}

