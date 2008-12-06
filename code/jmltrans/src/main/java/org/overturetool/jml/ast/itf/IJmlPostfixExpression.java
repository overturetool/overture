package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlPostfixExpression extends IJmlExpression
{
	abstract Vector getPrimary() throws CGException;
	abstract IJmlPostfixOperation getOperation() throws CGException;
}

