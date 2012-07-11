package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlIfStatement extends IOmlStatement
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract IOmlStatement getThenStatement() throws CGException;
	abstract Vector getElseifStatement() throws CGException;
	abstract IOmlStatement getElseStatement() throws CGException;
	abstract Boolean hasElseStatement() throws CGException;
}

