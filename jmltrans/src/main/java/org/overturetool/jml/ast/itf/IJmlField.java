package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlField extends IJmlNode
{
	abstract String getId() throws CGException;
	abstract IJmlType getType() throws CGException;
}

