package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCasesStatement extends IOmlStatement
{
	abstract IOmlExpression getMatchExpression() throws CGException;
	abstract Vector getAlternativeList() throws CGException;
	abstract IOmlStatement getOthersStatement() throws CGException;
	abstract Boolean hasOthersStatement() throws CGException;
}

