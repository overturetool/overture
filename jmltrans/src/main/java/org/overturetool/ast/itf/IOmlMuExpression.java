package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMuExpression extends IOmlExpression
{
	abstract IOmlExpression getExpression() throws CGException;
	abstract Vector getModifierList() throws CGException;
}

