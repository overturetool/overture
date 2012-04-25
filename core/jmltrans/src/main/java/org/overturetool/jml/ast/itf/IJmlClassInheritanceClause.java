package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlClassInheritanceClause extends IJmlNode
{
	abstract String getIdentifierList() throws CGException;
}

