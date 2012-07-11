package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlMapValueToValueType extends IJmlType
{
	abstract IJmlType getDomType() throws CGException;
	abstract IJmlType getRngType() throws CGException;
}

