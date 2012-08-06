package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlQuoteType extends IOmlType
{
	abstract IOmlQuoteLiteral getQuoteLiteral() throws CGException;
}

