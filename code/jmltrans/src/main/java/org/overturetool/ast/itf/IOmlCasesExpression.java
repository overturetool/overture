package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCasesExpression extends IOmlExpression
{
	abstract IOmlExpression getMatchExpression() throws CGException;
	abstract Vector getAlternativeList() throws CGException;
	abstract IOmlExpression getOthersExpression() throws CGException;
	abstract Boolean hasOthersExpression() throws CGException;
}

