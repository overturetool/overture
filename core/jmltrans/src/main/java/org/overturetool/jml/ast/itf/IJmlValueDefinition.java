package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlValueDefinition extends IJmlNode
{
	abstract IJmlAccessDefinition getAccess() throws CGException;
	abstract Boolean getStaticMod() throws CGException;
	abstract Boolean getFinalMod() throws CGException;
	abstract IJmlValueShape getShape() throws CGException;
}

