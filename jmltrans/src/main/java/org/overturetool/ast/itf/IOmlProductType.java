package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlProductType extends IOmlType
{
	abstract IOmlType getLhsType() throws CGException;
	abstract IOmlType getRhsType() throws CGException;
}

