package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlFunctionTrailer extends IOmlNode
{
	abstract IOmlExpression getPreExpression() throws CGException;
	abstract Boolean hasPreExpression() throws CGException;
	abstract IOmlExpression getPostExpression() throws CGException;
	abstract Boolean hasPostExpression() throws CGException;
}

