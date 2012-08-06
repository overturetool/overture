package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlEnumerationType extends IJmlType
{
	abstract IJmlEnumLiteral getEnumLiteral() throws CGException;
}

