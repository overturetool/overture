package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTraceRange extends IOmlTraceRepeatPattern
{
	abstract IOmlNumericLiteral getLower() throws CGException;
	abstract IOmlNumericLiteral getUpper() throws CGException;
	abstract Boolean hasUpper() throws CGException;
}

