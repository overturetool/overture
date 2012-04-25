package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlMapLet extends IJmlNode
{
	abstract IJmlExpression getDomVal() throws CGException;
	abstract IJmlExpression getRngVal() throws CGException;
}

