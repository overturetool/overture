package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlPartialFunctionType extends IOmlType
{
	abstract IOmlType getDomType() throws CGException;
	abstract IOmlType getRngType() throws CGException;
}

