package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlField extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract Boolean hasIdentifier() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract Boolean getIgnore() throws CGException;
}

