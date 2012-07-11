package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSpecificationStatement extends IOmlStatement
{
	abstract IOmlExternals getExternals() throws CGException;
	abstract Boolean hasExternals() throws CGException;
	abstract IOmlExpression getPreExpression() throws CGException;
	abstract Boolean hasPreExpression() throws CGException;
	abstract IOmlExpression getPostExpression() throws CGException;
	abstract IOmlExceptions getExceptions() throws CGException;
	abstract Boolean hasExceptions() throws CGException;
}

